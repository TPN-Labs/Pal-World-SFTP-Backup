import os

DISCORD_HOOK: str = os.environ["DISCORD_HOOK"]
S3_BASE_URL: str = "https://little-palo.s3.eu-central-1.amazonaws.com/uploads/"
S3_BUCKET: str = os.environ["S3_BUCKET"]
SFTP_HOST: str = os.environ["SFTP_HOST"]
SFTP_USER: str = os.environ["SFTP_USER"]
SFTP_PASS: str = os.environ["SFTP_PASS"]
SFTP_PORT: int = int(os.environ["SFTP_PORT"])