package com.tpnlabs.palo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.tpnlabs.palo.vendors.S3Service;
import com.tpnlabs.palo.vendors.SFTPService;
import com.tpnlabs.palo.vendors.ZipService;
import software.amazon.awssdk.regions.Region;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Palo implements RequestHandler<Map<String,String>, String> {

    private static final S3Service s3Service = new S3Service(Region.of(System.getenv("AWS_REGION")));
    private static final long START_TIME = System.currentTimeMillis();
    private static final String S3_BUCKET = System.getenv("S3_BUCKET");

    private int getTotalS3Backups() {
        if(!s3Service.doesBucketExist(S3_BUCKET)) {
            throw new PaloException("Bucket " + S3_BUCKET + " does not exist");
        }

        return s3Service.getTotalBackups(S3_BUCKET);
    }

    public static void main(String[] args) {
        LocalDateTime nowDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm");

        SFTPService sftpService = new SFTPService();
        //sftpService.download();
        sftpService.disconnect();
    }

    @Override
    public String handleRequest(Map<String, String> eventMap, Context context) {
        LocalDateTime nowDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm");
        String archiveName = "backup_" + nowDate.format(formatter) + ".zip";
        int totalS3Backups = getTotalS3Backups();

        SFTPService sftpService = new SFTPService();
        sftpService.download();
        sftpService.disconnect();

        try {
            ZipService.create("backups", archiveName);
        } catch (Exception e) {
            throw new PaloException("Error creating zip file: " + e.getMessage());
        }

        s3Service.putObject(S3_BUCKET, "uploads", archiveName);

        int totalExecutionTime = (int) ((System.currentTimeMillis() - START_TIME) / 1000);
        long fileSize = Paths.get(archiveName).toFile().length();
        Utils.sendDiscordMessage(archiveName, totalS3Backups, fileSize, totalExecutionTime);
        return "I love Palworld!";
    }
}