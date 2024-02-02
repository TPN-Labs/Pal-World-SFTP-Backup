import boto3
from boto3 import Session

from env import S3_BUCKET
from utils.exceptions import PaloS3BucketInvalidException


class S3Service:
    def __init__(self):
        self.session: Session = boto3.Session()
        self.s3 = self.session.resource('s3')
        self.palo_bucket = self.s3.Bucket(S3_BUCKET)

    def __does_bucket_exist(self) -> bool:
        return self.palo_bucket.creation_date is not None

    def get_total_backups(self) -> int:
        if not self.__does_bucket_exist():
            raise PaloS3BucketInvalidException

        return len(list(self.palo_bucket.objects.all())) - 1

    def upload_archive(self, file_path: str, key: str):
        self.palo_bucket.upload_file(file_path, key)
