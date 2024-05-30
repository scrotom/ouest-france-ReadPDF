package com.readpdfaffichette.version1.service;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class FtpService {

    private static final Logger logger = Logger.getLogger(FtpService.class.getName());
    private FTPClient ftpClient;

    public FtpService() {
        this.ftpClient = new FTPClient();
    }

    public void uploadFileToFTP(String server, int port, String user, String pass, String filePath, String uploadPath) throws IOException {
        try {
            logger.info("Connecting to FTP server...");
            ftpClient.connect(server, port);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                throw new IOException("Could not connect to FTP server. Reply Code: " + replyCode);
            }
            logger.info("Connected to FTP server.");

            boolean success = ftpClient.login(user, pass);
            if (!success) {
                throw new IOException("Could not login to FTP server.");
            }
            logger.info("Logged in to FTP server.");

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            try (InputStream inputStream = new FileInputStream(filePath)) {
                logger.info("Uploading file...");
                boolean done = ftpClient.storeFile(uploadPath, inputStream);
                if (!done) {
                    throw new IOException("Could not upload the file to the FTP server.");
                }
                logger.info("File uploaded successfully.");
            }
            ftpClient.logout();
            logger.info("Logged out from FTP server.");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error uploading file to FTP server: " + ex.getMessage(), ex);
            throw new IOException("Error uploading file to FTP server: " + ex.getMessage(), ex);
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                    logger.info("Disconnected from FTP server.");
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "Error disconnecting from FTP server: " + ex.getMessage(), ex);
                }
            }
        }
    }
}
