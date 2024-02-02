package com.tpnlabs.palo.vendors;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class S3Service {

    private final String backupPrefix = "uploads/";
    private S3Client s3Client;

    public S3Service(Region awsRegion) {
        init(awsRegion);
    }

    private void init(Region awsRegion) {
        this.s3Client = S3Client.builder()
            .region(awsRegion)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }

    public boolean doesBucketExist(String bucketName) {
        HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
            .bucket(bucketName)
            .build();

        try {
            s3Client.headBucket(headBucketRequest);
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }

    public PutObjectResponse putObject(String bucketName, String key, String file) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key + "/" + file)
                .build();


        return s3Client.putObject(request, Paths.get(file));
    }

    public int getTotalBackups(String bucketName) {
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
            .bucket(bucketName)
            .prefix(backupPrefix)
            .build();

        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);

        // -1 because the prefix is also counted as an object
        return listObjectsV2Response.keyCount() - 1;
    }
}