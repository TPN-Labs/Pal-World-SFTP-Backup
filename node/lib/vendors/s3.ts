import { S3Client as AWS_S3, ListObjectsV2Command, HeadBucketCommand, PutObjectCommand } from '@aws-sdk/client-s3';
import { AWS_REGION } from '../env';
import { readFileSync } from 'node:fs';

export class S3Client {
    private s3: AWS_S3;
    private readonly bucketName: string;

    constructor(bucketName: string) {
        this.s3 = new AWS_S3({ region: AWS_REGION });
        this.bucketName = bucketName;
    }

    async getTotalItems(): Promise<number> {
        const command = new ListObjectsV2Command({ Bucket: this.bucketName });
        const response = await this.s3.send(command);
        return response.KeyCount || 0;
    }

    async doesBucketExist(): Promise<boolean> {
        const command = new HeadBucketCommand({ Bucket: this.bucketName });
        try {
            await this.s3.send(command);
            return true;
        } catch (error) {
            return false;
        }
    }

    async uploadFile(filePath: string, key: string): Promise<void> {
        const fileContent = readFileSync(filePath);
        const command = new PutObjectCommand({
            Bucket: this.bucketName,
            Key: key,
            Body: fileContent,
        });
        await this.s3.send(command);
    }
}
