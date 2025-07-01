package org.jhcg.pms.service;

import com.google.protobuf.ByteString;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jhcg.pms.api.FileManageProto;
import org.jhcg.pms.api.ModelCallProto;
import org.jhcg.pms.client.*;
import org.jhcg.pms.ui.model.CandidatesTableRow;
import org.jhcg.pms.ui.sence.CandidatesAnchorPaneSence;
import org.jhcg.pms.ui.sence.MainSence;
import org.jhcg.pms.util.SqliteUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Charles
 * @since JDK
 */
public class ServiceCenter {
    //        private static final String IP = "127.0.0.1";
    private static final String IP = "192.168.10.99";
    private static final int PORT = 5577;
    private static String dbPath = System.getProperty("user.home") + File.separator + ".pms" + File.separator + "db" + File.separator;


    public static void main(String[] args) {
        System.out.println(dbPath);
        clearCache();
    }

    /**
     * 创建数据库缓存文件夹
     */
    public static void createDBPathFolder() {
        File file = new File(dbPath);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
    }

    public static String getDbPath() {
        return dbPath;
    }

    //----------以下为grpc:FileManager服务----------

    public static void submitFile(String path) {
        //创建数据库缓存文件夹
        createDBPathFolder();
        FileManager.submitFile(IP, PORT, path, new CallableObserver<FileManageProto.SubmitFileResponse>() {
            private SqliteUtil sqliteUtil = null;

            @Override
            public void onNext(FileManageProto.SubmitFileResponse value) {
                try {
                    //创建数据和数据表
                    if (sqliteUtil == null) {
                        String dbFilePath = dbPath;
                        //创建helper对象，会打开与文件名同名的数据库文件，若文件不存在则创建
                        sqliteUtil = new SqliteUtil(dbFilePath + "cache.db");
                        //创建存放结果的表
                        String sql1 = "drop table if exists file_data;" +
                                "create table file_data" +
                                "(id integer," +
                                " file_name text," +
                                " title text," +
                                " mw float," +
                                " mz blob," +
                                " intensities blob," +
                                " inchikey text," +
                                "primary key(id,file_name)" +
                                ");";
                        sqliteUtil.getStatement().executeUpdate(sql1);
                    }

                    //插入数据行
                    String sql2 = "insert into file_data (id,file_name,title,mw,mz,intensities,inchikey) values(?,?,?,?,?,?,?);";

                    PreparedStatement ps = sqliteUtil.getPreparedStatement(sql2);
                    ps.setInt(1, value.getResult().getId());
                    ps.setString(2, value.getResult().getFileName());
                    ps.setString(3, value.getResult().getTitle());
                    ps.setFloat(4, value.getResult().getMw());
                    ps.setBytes(5, value.getResult().getMz().toByteArray());
                    ps.setBytes(6, value.getResult().getIntensities().toByteArray());
                    ps.setString(7, value.getResult().getInchikey());
                    ps.executeUpdate();

                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (sqliteUtil != null) {
                        sqliteUtil.destroyed();
                    }
                }
                //System.out.printf("%d %s\n",value.getResult().getId(),value.getResult().getTitle());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
            }
        });
    }

    public static void clearCache() {
        //清理前端缓存
        System.out.println(dbPath);
        File folder = new File(dbPath);
        deleteFiles(folder);
        //清理后端缓存
        FileManager.clearCache(IP, PORT);
    }

    /**
     * 删除文件夹及文件夹下的文件
     *
     * @param file File
     */
    private static void deleteFiles(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else {
                for (File f : file.listFiles()) {
                    deleteFiles(f);
                }
                file.delete();
            }
        }
    }


    //----------以下为grpc:ModelService服务----------
    public static byte[] plotMS(byte[] mz, byte[] intensities) {
        return ModelService.call(new PlotMS(), IP, PORT, mz, intensities);
    }

    public static byte[] plotMSAgainst(byte[] origMz, byte[] origIntensities, int candiIndex) {
        System.out.println("candiIndex:" + candiIndex);
        return ModelService.call(new PlotMSAgainst(), IP, PORT, origMz, origIntensities, candiIndex);
    }

    public static byte[] plotMolStruc(String smiles) {
        return ModelService.call(new PlotMolStruc(), IP, PORT, smiles);
    }

    public static void getCandidateMessage(String fileName, TabPane resultTabPane) {
        ModelService.call(new GetCandidateMessage(), IP, PORT, fileName, new CallableObserver<ModelCallProto.GetCandidateMessageResponse>() {
            private SqliteUtil sqliteUtil = null;

            @Override
            public void onNext(ModelCallProto.GetCandidateMessageResponse value) {
                FileOutputStream fos = null;
                try {
                    ByteString chunk = value.getChunk();
                    fos = new FileOutputStream(dbPath + "candidate.db", true);
                    fos.write(chunk.toByteArray());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
//                try {
//                    //创建数据和数据表
//                    if (sqliteUtil == null) {
//                        String dbFilePath = dbPath;
//                        //创建helper对象，会打开与文件名同名的数据库文件，若文件不存在则创建
//                        sqliteUtil = new SqliteUtil(dbFilePath + "cache.db");
//                        //创建存放结果的表
//                        String sql1 = "drop table if exists candidate_message;" +
//                                "create table candidate_message" +
//                                "(pid integer primary key autoincrement," +
//                                " id integer," +
//                                " file_name text," +
//                                " inchikey text," +
//                                " smiles text," +
//                                " rank integer," +
//                                " distance float," +
//                                " mw float," +
//                                " mz blob," +
//                                " intensities blob" +
//                                ");";
//                        sqliteUtil.getStatement().executeUpdate(sql1);
//                    }
//
//                    //插入数据行
//                    String sql2 = "insert into candidate_message (id,file_name,inchikey,smiles,rank,distance,mw,mz,intensities) values(?,?,?,?,?,?,?,?,?);";
//                    for (ModelCallProto.GetCandidateMessageResponse.Result result : value.getResultsList()) {
//                        for (ModelCallProto.GetCandidateMessageResponse.Candidate candidate : result.getCandidatesList()) {
//                            PreparedStatement ps = sqliteUtil.getPreparedStatement(sql2);
//                            ps.setInt(1, result.getId());
//                            ps.setString(2, result.getFileName());
//                            ps.setString(3, candidate.getInchikey());
//                            ps.setString(4, candidate.getSmiles());
//                            ps.setInt(5, candidate.getRank());
//                            ps.setFloat(6, candidate.getDistance());
//                            ps.setFloat(7, candidate.getMw());
//                            ps.setBytes(8, candidate.getMz().toByteArray());
//                            ps.setBytes(9, candidate.getIntensities().toByteArray());
//                            ps.executeUpdate();
//                        }
//
//                    }
//
//
//                } catch (ClassNotFoundException e) {
//                    throw new RuntimeException(e);
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                } finally {
//                    if (sqliteUtil != null) {
//                        sqliteUtil.destroyed();
//                    }
//                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                resultTabPane.getTabs().get(1).setDisable(false);
                new Thread(() -> {
                    refreshCandidatesTab(resultTabPane);
                });
            }

            public void refreshCandidatesTab(TabPane resultTabPane) {
                SqliteUtil sqliteUtil = null;
                ResultSet resultSet = null;
                try {
                    sqliteUtil = new SqliteUtil(ServiceCenter.getDbPath() + "candidate.db");

                    //2.刷新CandidatesTab
                    //2.0清空所有行
                    CandidatesAnchorPaneSence.clearCandidatesTableRows();
                    //2.1通过id查找到TableView对象
                    TableView tableView = (TableView) resultTabPane.lookup("#candidatesTableView");
                    //2.2获取表格列：TableColumn
                    TableColumn<CandidatesTableRow, String> inchikeyColumn = (TableColumn) tableView.getColumns().get(0);
                    TableColumn<CandidatesTableRow, String> smilesColumn = (TableColumn) tableView.getColumns().get(1);
                    TableColumn<CandidatesTableRow, Double> mwColumn = (TableColumn) tableView.getColumns().get(2);
                    TableColumn<CandidatesTableRow, Double> distanceColumn = (TableColumn) tableView.getColumns().get(3);
                    TableColumn<CandidatesTableRow, Integer> rankColumn = (TableColumn) tableView.getColumns().get(4);
                    //2.3设置列TableColumn与类CandidatesTableRow的关联性
                    inchikeyColumn.setCellValueFactory(new PropertyValueFactory<>("inchikey"));
                    smilesColumn.setCellValueFactory(new PropertyValueFactory<>("smiles"));
                    mwColumn.setCellValueFactory(new PropertyValueFactory<>("mw"));
                    distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
                    rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
                    //2.4绑定TableView行与CandidatesAnchorPaneSence.getCandidatesTableRows()
                    tableView.setItems(CandidatesAnchorPaneSence.getCandidatesTableRows());

                    String sql2 = "select pid,inchikey,smiles,mw,distance,rank from candidate_message where id=? and file_name=?;";
                    PreparedStatement ps2 = sqliteUtil.getPreparedStatement(sql2);
                    ps2.setInt(1, MainSence.getCurrentID());
                    ps2.setString(2, MainSence.getCurrentFileName());
                    resultSet = ps2.executeQuery();
                    if (resultSet.next()) {
                        do {
                            int pid = resultSet.getInt("pid");
                            String inchikey = resultSet.getString("inchikey");
                            String smiles = resultSet.getString("smiles");
                            double mw = resultSet.getDouble("mw");
                            BigDecimal bigMW = new BigDecimal(mw).setScale(3, RoundingMode.HALF_UP);
                            double distance = resultSet.getDouble("distance");
                            BigDecimal bigDistance = new BigDecimal(distance).setScale(4, RoundingMode.HALF_UP);
                            int rank = resultSet.getInt("rank");

                            // 添加表格行数据
                            CandidatesAnchorPaneSence.addCandidatesTableRow(new CandidatesTableRow(pid, inchikey, smiles, bigMW, bigDistance, rank));

                        } while (resultSet.next());

                    } else {
                        CandidatesAnchorPaneSence.clearCandidatesTableRows();
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (sqliteUtil != null) {
                        sqliteUtil.destroyed();
                    }
                }
            }

        });
    }

}
