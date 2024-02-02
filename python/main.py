import json
import os
from datetime import datetime

from env import DISCORD_HOOK, S3_BASE_URL
from utils.discord import DiscordService, EmbedObject
from vendors.s3 import S3Service
from vendors.sftp import SftpService

s3_service = S3Service()
sftp_service = SftpService()
discord_service = DiscordService(DISCORD_HOOK)
discord_embed = EmbedObject()


def run():
    now_date_time = datetime.now()
    d_total_backups = s3_service.get_total_backups()

    archive_name = f"backups_{now_date_time.strftime('%Y_%m_%d_%H_%M')}"
    d_archive_name = f"{archive_name}.zip"
    sftp_service.download_backup(archive_name)
    s3_service.upload_archive(d_archive_name, f"uploads/{d_archive_name}")

    d_execute_time = str(round((datetime.now() - now_date_time).total_seconds(), 1))
    d_archive_size = str(round(os.stat(d_archive_name).st_size / 1024 / 1024, 1))

    discord_service.set_content(f"Link: {S3_BASE_URL}{d_archive_name}")
    discord_embed.set_title("✅ Successfully backed up Pal World!")
    discord_embed.set_description(f"Archive name: {archive_name}\nTotal backups: {d_total_backups}")
    discord_embed.set_color(0x00FF00)
    discord_embed.add_field("Size", f"{d_archive_size} MB", True)
    discord_embed.add_field("Time", f"{d_execute_time} s", True)
    discord_service.add_embed(discord_embed)
    discord_service.execute()


def lambda_handler(event, context):
    run()
    return {
        'statusCode': 200,
        'body': json.dumps('I love Pal World!')
    }


if __name__ == "__main__":
    run()
