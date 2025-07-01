import pathlib
from tools.loader import load_from_mgf
from matchms.importing import load_from_msp
from matchms import Spectrum
from rdkit import Chem
from rdkit.Chem import Draw
from io import BytesIO
from matplotlib import pyplot as plt
from typing import Tuple, Generator
from . import mw_by_pim
from . import fastei
from . import deepei
from tqdm import tqdm
from collections import defaultdict
import heapq
from resources.global_var import general_var
import grpc
from api import subnode_call_pb2,subnode_call_pb2_grpc
import numpy as np
import pickle
from concurrent import futures


def get_candidate_message_distri(
    path: pathlib.Path,
) -> list:
    """分布式搜库

    Args:
        path (pathlib.Path): 质谱文件路径

    Return:
        List: 一组候选物信息
    """
    
    def create_channel(ip_port='localhost:50051'):
        """创建gRPC通道"""
        return grpc.insecure_channel(
            ip_port,
            options=[
                ('grpc.max_send_message_length', 50 * 1024 * 1024),
                ('grpc.max_receive_message_length', 50 * 1024 * 1024),
            ]
        )

    def generate_requests(numpy_data):
        """生成请求数据流"""
        buffer = BytesIO()
        np.save(buffer, numpy_data)
        data = buffer.getvalue()
        chunk_size = 49 * 1024 * 1024  # 49MB分块
        for i in range(0, len(data), chunk_size):
            yield subnode_call_pb2.GetCandidatesRequest(chunk=data[i:i+chunk_size])
    
    def call_single_node(ip_port, input_data: np.ndarray):
        """调用单个节点"""
        channel = None
        try:
            channel = create_channel(ip_port)
            stub = subnode_call_pb2_grpc.SubNodeCallServiceStub(channel)
            
            # 准备请求流
            request_stream = generate_requests(input_data)
            
            # 接收响应
            response_stream = stub.getCandidates(request_stream)
            
            # 组装响应数据
            result_buffer = BytesIO()
            for response in response_stream:
                result_buffer.write(response.chunk)
                
            result_buffer.seek(0)
            return pickle.loads(result_buffer.getvalue())
            
        except grpc.RpcError as e:
            raise RuntimeError(f"节点 {ip_port} 调用失败: {e.code().name}")
            # logging.error(f"节点 {ip_port} 调用失败: {e.code().name}")
            # raise
        finally:
            if channel:
                channel.close()

    def merge_and_sort_results(all_results, top_k: int = 100) -> np.ndarray:
        """
        合并并排序结果
        :param all_results: 所有节点的返回结果列表
        :param top_k: 每行保留的结果数量
        :return: 形状为n*100的numpy数组
        """
        # 按列合并 (axis=1)
        merged = []
        for row_results in zip(*all_results):
            # 合并所有节点返回的当前行结果
            combined_row = []
            for node_result in row_results:
                combined_row.extend(node_result)
            
            # 按distance排序并取top_k
            sorted_row = sorted(combined_row, key=lambda x: x['distance'])[:top_k]
            merged.append(sorted_row)
        
        return np.array(merged, dtype=object)
    
    def concurrent_call_nodes(ip_ports, input_data: np.ndarray) -> np.ndarray:
        """
        并发调用多个节点并返回处理后的结果
        :param ip_ports: 节点地址列表 如 ["localhost:50051", "localhost:50052"]
        :param input_data: 输入numpy数组
        :return: 合并排序后的n*100结果数组
        """
        with futures.ThreadPoolExecutor(max_workers=len(ip_ports)) as executor:
            # 提交所有节点调用任务
            future_to_ip = {
                executor.submit(call_single_node, ip_port, input_data): ip_port
                for ip_port in ip_ports
            }
            
            all_results = []
            for future in futures.as_completed(future_to_ip):
                ip_port = future_to_ip[future]
                try:
                    result = future.result()
                    all_results.append(result)
                    # logging.info(f"节点 {ip_port} 调用成功，返回 {len(result)} 行数据")
                except Exception as e:
                    raise RuntimeError(f"节点 {ip_port} 调用异常: {str(e)}")
                    # logging.error(f"节点 {ip_port} 调用异常: {str(e)}")
                    # raise
        
        # 合并和排序结果
        return merge_and_sort_results(all_results)
    
    # 节点列表
    num_nodes = 10
    nodes = [f"localhost:{50051 + i}" for i in range(num_nodes)]
    # nodes = ["localhost:50051", "localhost:50052", "localhost:50053"]
    
    file_suffix = path.suffix
    if file_suffix == ".mgf":
        spectrums = load_from_mgf(str(path))
    elif file_suffix == ".msp":
        spectrums = load_from_msp(str(path))
    else:
        assert False, "input file should be .msp or .mgf file"
    spectrums = list(spectrums)

    
    from matchms.filtering import normalize_intensities
    from resources.global_var import general_var,fastei_var
    from spec2vec import SpectrumDocument
    print("==========fastei:get all vector of unknown_spectra==========")
    # 数据编码
    word2vectors = []
    for i in tqdm(range(len(spectrums))):
        spectrum_in = SpectrumDocument(normalize_intensities(spectrums[i]), n_decimals=0)
        vetors = fastei_var["spectovec"]._calculate_embedding(spectrum_in)

        word2vectors.append(vetors)
    xq = np.array(word2vectors).astype("float32")

    xq_len = np.linalg.norm(xq, axis=1, keepdims=True)
    xq = xq / xq_len
    
    from resources.global_var import general_var,deepei_var
    import torch
    print("==========deepei:get all fingerprints of unknown_spectra==========")
    def ms2vec(peakindex, peakintensity, maxmz=2000):
        output = np.zeros(maxmz)
        for i, j in enumerate(peakindex):
            if round(j) >= maxmz:
                continue
            output[int(round(j))] = float(peakintensity[i])
        output = output / (max(output) + 10**-6)
        return output
    
    def predict_fingerprint(specs, model_list):
        pred_fp = np.zeros((specs.shape[0], len(model_list)))
        
        for i, m in enumerate(tqdm(model_list)):
            pred = m.predict(torch.tensor(specs,dtype=torch.float32))
            pred_fp[:, i] = pred
        return pred_fp
    
    unknown_peak_vecs = []
    for spec in tqdm(spectrums):
        unknown_peak_vecs.append(ms2vec(spec.mz, spec.intensities))
    unknown_peak_vecs = np.array(unknown_peak_vecs)
    pred_fps = predict_fingerprint(unknown_peak_vecs, deepei_var["model_list"])
    
    combined = np.concatenate([xq, pred_fps], axis=1)
    # 并发调用
    final_result = concurrent_call_nodes(nodes, combined)
    print(f"最终结果形状: {final_result.shape}")  # 应为 (n, 100)
    
    # 对每一个候选物进行处理：增加元数据
    for final_candis in tqdm(final_result):
        for j, candi in enumerate(final_candis):
            candi["rank"] = j
            candi["smiles"] = str(general_var["smi_list"][candi["candi_index"]])
            candi["mw"] = float(general_var["mw_list"][candi["candi_index"]])
            candi["inchikey"] = str(general_var["inchikey_list"][candi["candi_index"]])
    
    return final_result


def list_get(lst, index, default=None):
    try:
        return lst[index]
    except (IndexError, TypeError):  # 处理索引越界或非列表输入
        return default
    
def float_(number, default=None):
    try:
        return float(number)
    except (IndexError, TypeError):  # 处理索引越界或非列表输入
        return default


def get_candidate_message(
    path: pathlib.Path,
) -> list:
    """返回一个列表，每一项都是一组候选物信息

    Args:
        path (pathlib.Path): 质谱文件路径

    Return:
        List: 一组候选物信息
    """

    file_suffix = path.suffix
    if file_suffix == ".mgf":
        spectrums = load_from_mgf(str(path))
    elif file_suffix == ".msp":
        spectrums = load_from_msp(str(path))
    else:
        assert False, "input file should be .msp or .mgf file"
    spectrums = list(spectrums)

    results_fastei = get_FastEI(spectrums)
    results_deepei = get_deepei(spectrums)

    weight_rate1 = 0.5  # fastei weight
    weight_rate2 = 1 - weight_rate1  # deepei weight
    
    final_candis_list = [] #暂存所有结果
    for i, spec in enumerate(tqdm(spectrums)):

        res_fast = results_fastei[i]
        res_deep = results_deepei[i]
        # 比较算法
        fast_dict = defaultdict(list)
        for candi in res_fast["candidates"]:
            fast_dict[candi["inchikey"][:14]].append(candi)

        deep_dict = defaultdict(list)
        for candi in res_deep["candidates"]:
            deep_dict[candi["inchikey"][:14]].append(candi)

        fast_keys = set(fast_dict.keys())
        deep_keys = set(deep_dict.keys())

        # 找到匹配和没有匹配的fast_keys和deep_keys
        intersection_keys = deep_keys & fast_keys
        fast_unmatcheds = fast_keys - intersection_keys
        deep_unmatcheds = deep_keys - intersection_keys

        final_candis = []  # 存储最后结果的列表
        
        for intersection_key in intersection_keys:
            fast_candi = fast_dict[intersection_key]
            deep_candi = deep_dict[intersection_key]
            final_candis.append(
                {
                    "candi_index": fast_candi[0]["candi_index"],
                    "distance": fast_candi[0]["distance"] * weight_rate1 + deep_candi[0]["distance"] * weight_rate2,
                    # "smiles": fast_candi[0]["smiles"],
                    # "mw": fast_candi[0]["mw"],
                    # "inchikey": fast_candi[0]["inchikey"],
                }
            )

        for fast_unmatched in fast_unmatcheds:
            fast_candi = fast_dict[fast_unmatched]
            final_candis.append(
                {
                    "candi_index": fast_candi[0]["candi_index"],
                    "distance": fast_candi[0]["distance"] * weight_rate1 + weight_rate2,
                    # "smiles": fast_candi[0]["smiles"],
                    # "mw": fast_candi[0]["mw"],
                    # "inchikey": fast_candi[0]["inchikey"],
                }
            )

        for deep_unmatched in deep_unmatcheds:
            deep_candi = deep_dict[deep_unmatched]
            final_candis.append(
                {
                    "candi_index": deep_candi[0]["candi_index"],
                    "distance": deep_candi[0]["distance"] * weight_rate2 + weight_rate1,
                    # "smiles": deep_candi[0]["smiles"],
                    # "mw": deep_candi[0]["mw"],
                    # "inchikey": deep_candi[0]["inchikey"],
                }
            )


        final_candis = heapq.nsmallest(100, final_candis, key=lambda x: x["distance"])
        

        
        # 对每一个候选物进行处理：增加rank等信息
        for j, candi in enumerate(final_candis):
            candi["rank"] = j
            candi["smiles"] = str(list_get(general_var["smi_list"],candi["candi_index"]))
            candi["mw"] = float_(list_get(general_var["mw_list"],candi["candi_index"]))
            candi["inchikey"] = str(list_get(general_var["inchikey_list"],candi["candi_index"]))
            
        
        final_candis_list.append(final_candis)

    return final_candis_list
    

def get_plot_ms(mz, intensities) -> bytes:
    """返回一个化合物的质谱图

    Args:
        mz: 质核比
        intensities : 强度

    Yields:
        bytes:二进制图片数据
    """
    try:
        spec = Spectrum(
            mz=mz,
            intensities=intensities,
            metadata=None,
            metadata_harmonization=True,
        )
        # 关闭matplotlib交互模式
        if plt.isinteractive():
            plt.ioff()
        spec.plot()
        buffer = BytesIO()
        plt.savefig(buffer, format="png")
        # plt.close()
        return buffer.getvalue()
    except:
        return None


def get_plot_ms_against(orig_mz, orig_intensities, mz, intensities) -> bytes:
    """返回一个对比质谱图

    Args:
        orig_mz: 质核比1
        orig_intensities: 强度1
        mz: 质核比2
        intensities : 强度2

    Yields:
        bytes:二进制图片数据
    """
    try:
        orig_spec = Spectrum(
            mz=orig_mz,
            intensities=orig_intensities,
            metadata=None,
            metadata_harmonization=True,
        )
        spec = Spectrum(
            mz=mz,
            intensities=intensities,
            metadata=None,
            metadata_harmonization=True,
        )
        # 关闭matplotlib交互模式
        if plt.isinteractive():
            plt.ioff()
        buffer = BytesIO()
        orig_spec.plot_against(spec)
        plt.savefig(buffer, format="png")
        # plt.close()
        return buffer.getvalue()
    except:
        return None


def get_plot_mol_struc(smiles: str) -> bytes:
    """返回一个化合物的分子结构图

    Args:
        smiles: 质谱文件路径

    Yields:
        bytes: 二进制图片数据
    """
    try:
        mol = Chem.MolFromSmiles(smiles)
        image = Draw.MolToImage(mol)
        buffer = BytesIO()
        image.save(buffer, format="png")
        return buffer.getvalue()
    except:
        return None


def get_mw_by_pim(path: pathlib.Path) -> list:
    """调用mw_by_pim，并返回结果

    Args:
        path (pathlib.Path): 质谱文件路径

    Returns:
        list:每一项都是一张质谱的预测结果
    """
    result = mw_by_pim.predict(str(path))
    # result_json = json.dumps(result)
    return result


def get_FastEI(specs: list) -> list:

    return fastei.predict(specs)


def get_deepei(specs: list) -> list:

    return deepei.predict(specs)
