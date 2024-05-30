package com.readpdfaffichette.version1.service;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FtpService {

    public void uploadFileToFTP(String server, int port, String user, String pass, String filePath, String uploadPath) throws IOException {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                throw new IOException("Could not connect to FTP server. Reply Code: " + replyCode);
            }

            boolean success = ftpClient.login(user, pass);
            if (!success) {
                throw new IOException("Could not login to FTP server.");
            }

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            try (InputStream inputStream = new FileInputStream(filePath)) {
                boolean done = ftpClient.storeFile(uploadPath, inputStream);
                if (!done) {
                    throw new IOException("Could not upload the file to the FTP server.");
                }
            }

            ftpClient.logout();
        } catch (IOException ex) {
            throw new IOException("Error uploading file to FTP server: " + ex.getMessage(), ex);
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
