from utils.logger import palo_logger


class PaloException(Exception):
    """Base class for exceptions in this module"""
    pass


class PaloS3BucketInvalidException(PaloException):
    """Raised when the S3 bucket is invalid"""

    def __init__(self):
        super().__init__("[PALO] S3 bucket is invalid")
