package com.tpnlabs.palo.vendors;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.tpnlabs.palo.PaloException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

public class SFTPService {

    private JSch jsch;
    private Session sftpSession;
    private ChannelSftp sftpChannel;

    private static final String sftpRemoteDirectory = "/Pal/Saved";
    private static final String localDirectory = "backups";
    private static final String SFTP_HOST = System.getenv("SFTP_HOST");
    private static final int SFTP_PORT = System.getenv("SFTP_PORT") != null ?
            Integer.parseInt(System.getenv("SFTP_PORT")) : 2022;
    private static final String SFTP_USERNAME = System.getenv("SFTP_USERNAME");
    private static final String SFTP_PASSWORD = System.getenv("SFTP_PASSWORD");

    public SFTPService() {
        init();
    }

    private void init() {
        createLocalDirectory();
        this.jsch = new JSch();

        String hostKnownPublicKey = "[wings.cloudforest.ro]:2022 ssh-ed25519 " +
                "AAAAC3NzaC1lZDI1NTE5AAAAIKflbZkuPyylKCxKzqsifq+7Wak122Lb+MjmZmYi3mRZ";

        try {
            this.jsch.setKnownHosts(new ByteArrayInputStream(hostKnownPublicKey.getBytes()));
            this.sftpSession = jsch.getSession(SFTP_USERNAME, SFTP_HOST, SFTP_PORT);
            connect();
        } catch (Exception e) {
            throw new PaloException("Error initializing SFTP service: " + e.getMessage());
        }
    }

    private void connect() {
        try {
            sftpSession.setConfig("StrictHostKeyChecking", "no");
            sftpSession.setConfig("server_host_key", "ssh-ed25519");
            sftpSession.setConfig("PreferredAuthentications", "password,publickey");
            sftpSession.setPassword(SFTP_PASSWORD);
            sftpSession.connect();

            Channel channel = sftpSession.openChannel("sftp");
            channel.connect();
            this.sftpChannel = (ChannelSftp) channel;

        } catch (JSchException e) {
            throw new PaloException("Error connecting to SFTP server: " + e.getMessage());
        }
    }

    private void createLocalDirectory() {
        File localDirFile = new File(SFTPService.localDirectory);
        if (!localDirFile.exists()) {
            localDirFile.mkdirs();
        }
    }

    private boolean checkIfFileExists(ChannelSftp sftpChannel, String fileName) {
        try {
            return !sftpChannel.ls(fileName).isEmpty();
        } catch (SftpException e) {
            return false;
        }
    }

    private void downloadDirectory(ChannelSftp sftpChannel, String remoteDir, String localDir) {
        File localDirFile = new File(localDir);
        if (!localDirFile.exists()) {
            localDirFile.mkdirs();
        }

        try {
            sftpChannel.cd(remoteDir);
            Vector<ChannelSftp.LsEntry> fileAndFolderList = sftpChannel.ls(remoteDir);

            for (ChannelSftp.LsEntry item : fileAndFolderList) {
                if (!item.getAttrs().isDir()) {
                    // Download file.
                    if(checkIfFileExists(sftpChannel, item.getFilename())) {
                        File localFile = new File(localDir, item.getFilename());
                        try (FileOutputStream fos = new FileOutputStream(localFile)) {
                            sftpChannel.get(item.getFilename(), fos);
                        } catch (Exception e) {
                            System.out.println("Exception encountered " + e);
                            System.out.println("Error downloading file: " + e.getMessage());
                        }
                    }
                } else if (!item.getFilename().equals(".") && !item.getFilename().equals("..")) {
                    // Download directory.
                    new File(localDir, item.getFilename()).mkdirs();
                    downloadDirectory(sftpChannel, remoteDir + "/" + item.getFilename(), localDir + "/" + item.getFilename());
                }
            }
        } catch (SftpException e) {
            System.out.println("Params: " + remoteDir);
            throw new PaloException("Error downloading directory: " + e.getMessage());
        }
    }

    public void disconnect() {
        sftpChannel.exit();
        sftpSession.disconnect();
    }

    public void download() {
        try {
            this.sftpChannel.cd(sftpRemoteDirectory);
        } catch (SftpException e) {
            throw new PaloException("Error changing directory to " + sftpRemoteDirectory + " : " + e.getMessage());
        }
        downloadDirectory(sftpChannel, sftpRemoteDirectory, localDirectory);
    }
}
