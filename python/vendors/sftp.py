import os
import shutil
from stat import S_ISDIR, S_ISREG

import paramiko
from paramiko.ssh_exception import AuthenticationException

from env import SFTP_HOST, SFTP_USER, SFTP_PASS, SFTP_PORT
from utils.logger import palo_logger


def zip_dir(dir_path, zip_file):
    root_dir = os.path.dirname(os.path.abspath(dir_path))
    base_dir = os.path.basename(dir_path)
    shutil.make_archive(zip_file, 'zip', root_dir, base_dir)


class SftpService:
    def __init__(self):
        self.client = None
        self.host = SFTP_HOST
        self.port = SFTP_PORT
        self.username = SFTP_USER
        self.password = SFTP_PASS
        self.remote_dir = "/Pal/Saved"
        self.local_dir = "backups"

    def __connect(self):
        self.client = paramiko.SSHClient()
        self.client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        self.client.connect(self.host, username=self.username, password=self.password, port=self.port,
                            allow_agent=False, look_for_keys=False, timeout=30)

    def __disconnect(self):
        self.client.close()

    def __download_dir(self, remote_dir, local_dir, sftp):
        try:
            os.mkdir(local_dir)
        except OSError:
            pass

        palo_logger.info(f"Downloading {remote_dir} to {local_dir}")
        for entry in sftp.listdir_attr(remote_dir):
            remote_path = remote_dir + "/" + entry.filename
            local_path = os.path.join(local_dir, entry.filename)
            mode = entry.st_mode
            if S_ISDIR(mode):
                try:
                    os.mkdir(local_path)
                except OSError:
                    pass
                self.__download_dir(remote_path, local_path, sftp)
            elif S_ISREG(mode):
                try:
                    sftp.get(remote_path, local_path)
                except FileNotFoundError:
                    pass

    def download_backup(self, archive_name):
        self.__connect()
        sftp = self.client.open_sftp()
        self.__download_dir(self.remote_dir, self.local_dir, sftp)
        sftp.close()
        self.__disconnect()

        zip_dir(self.local_dir, archive_name)
