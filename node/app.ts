import { Handler } from 'aws-lambda';
import { AWS_BUCKET, AWS_REGION, SFTP_HOST, SFTP_PASS, SFTP_PORT, SFTP_USER } from './lib/env';
//import { S3Client } from './lib/vendors/s3';
import { SftpClient } from './lib/vendors/sftp';

export const handler: Handler = async (event, context) => {
    //const s3Client = new S3Client(AWS_BUCKET);
    //const totalBackups = await s3Client.getTotalItems();
    //console.log(`Total backups: ${totalBackups}`);
    const sftpClient = new SftpClient(SFTP_HOST, SFTP_USER, SFTP_PASS, SFTP_PORT);
    await sftpClient.connect();
    await sftpClient.downloadFolder('/Pal/Saved', 'backups');
    console.log('ok');
};
