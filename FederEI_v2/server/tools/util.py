import pathlib


class CheckFileException(Exception):
    def __init__(self, status: str):
        self.status = status

    def __str__(self):
        return f"{self.status}"


def check_path(path: pathlib.Path):
    if not path.exists():
        raise CheckFileException(f"路径不存在！({str(path)})")
    if not path.is_file():
        raise CheckFileException("路径存在，但不是文件！")


def delete_files(path: pathlib.Path):
    if path.exists():
        if path.is_file():
            path.unlink()
        else:
            for item in path.iterdir():
                delete_files(item)
            path.rmdir()
