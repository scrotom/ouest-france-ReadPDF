package com.readpdfaffichette.version1.service;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FtpServiceTest {

    @Mock
    private FTPClient ftpClient;

    @InjectMocks
    private FtpService ftpService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test successful file upload to FTP server")
    public void testUploadFileToFTPSuccess() throws IOException {
        String server = "ftp.example.com";
        int port = 21;
        String user = "ftpuser";
        String pass = "ftppassword";

        // Create a temporary file for the test
        Path tempFilePath = Files.createTempFile("testfile", ".txt");
        Files.write(tempFilePath, "This is a test file.".getBytes());

        String filePath = tempFilePath.toString();
        String uploadPath = "/remote/path/testfile.txt";

        when(ftpClient.getReplyCode()).thenReturn(FTPReply.COMMAND_OK);
        when(ftpClient.login(anyString(), anyString())).thenReturn(true);
        when(ftpClient.storeFile(anyString(), any(InputStream.class))).thenReturn(true);

        doNothing().when(ftpClient).connect(anyString(), anyInt());

        ftpService.uploadFileToFTP(server, port, user, pass, filePath, uploadPath);

        verify(ftpClient, times(1)).connect(server, port);
        verify(ftpClient, times(1)).login(user, pass);
        verify(ftpClient, times(1)).enterLocalPassiveMode();
        verify(ftpClient, times(1)).setFileType(FTP.BINARY_FILE_TYPE);
        verify(ftpClient, times(1)).storeFile(eq(uploadPath), any(InputStream.class));
        verify(ftpClient, times(1)).logout();

        // Delete the temporary file after the test
        Files.deleteIfExists(tempFilePath);
    }

    @Test
    @DisplayName("Test failed connection to FTP server")
    public void testUploadFileToFTPConnectionFailure() throws IOException {
        String server = "ftp.example.com";
        int port = 21;
        String user = "ftpuser";
        String pass = "ftppassword";

        // Create a temporary file for the test
        Path tempFilePath = Files.createTempFile("testfile", ".txt");
        Files.write(tempFilePath, "This is a test file.".getBytes());

        String filePath = tempFilePath.toString();
        String uploadPath = "/remote/path/testfile.txt";

        when(ftpClient.getReplyCode()).thenReturn(FTPReply.SERVICE_NOT_AVAILABLE);

        doNothing().when(ftpClient).connect(anyString(), anyInt());

        assertThrows(IOException.class, () -> {
            ftpService.uploadFileToFTP(server, port, user, pass, filePath, uploadPath);
        });

        verify(ftpClient, times(1)).connect(server, port);
        verify(ftpClient, never()).login(anyString(), anyString());

        // Delete the temporary file after the test
        Files.deleteIfExists(tempFilePath);
    }

    @Test
    @DisplayName("Test failed login to FTP server")
    public void testUploadFileToFTPLoginFailure() throws IOException {
        String server = "ftp.example.com";
        int port = 21;
        String user = "ftpuser";
        String pass = "ftppassword";

        // Create a temporary file for the test
        Path tempFilePath = Files.createTempFile("testfile", ".txt");
        Files.write(tempFilePath, "This is a test file.".getBytes());

        String filePath = tempFilePath.toString();
        String uploadPath = "/remote/path/testfile.txt";

        when(ftpClient.getReplyCode()).thenReturn(FTPReply.COMMAND_OK);
        when(ftpClient.login(anyString(), anyString())).thenReturn(false);

        doNothing().when(ftpClient).connect(anyString(), anyInt());

        assertThrows(IOException.class, () -> {
            ftpService.uploadFileToFTP(server, port, user, pass, filePath, uploadPath);
        });

        verify(ftpClient, times(1)).connect(server, port);
        verify(ftpClient, times(1)).login(user, pass);
        verify(ftpClient, never()).enterLocalPassiveMode();
        verify(ftpClient, never()).setFileType(FTP.BINARY_FILE_TYPE);

        // Delete the temporary file after the test
        Files.deleteIfExists(tempFilePath);
    }

    @Test
    @DisplayName("Test failed file upload to FTP server")
    public void testUploadFileToFTPFailure() throws IOException {
        String server = "ftp.example.com";
        int port = 21;
        String user = "ftpuser";
        String pass = "ftppassword";

        // Create a temporary file for the test
        Path tempFilePath = Files.createTempFile("testfile", ".txt");
        Files.write(tempFilePath, "This is a test file.".getBytes());

        String filePath = tempFilePath.toString();
        String uploadPath = "/remote/path/testfile.txt";

        when(ftpClient.getReplyCode()).thenReturn(FTPReply.COMMAND_OK);
        when(ftpClient.login(anyString(), anyString())).thenReturn(true);
        when(ftpClient.storeFile(anyString(), any(InputStream.class))).thenReturn(false);

        doNothing().when(ftpClient).connect(anyString(), anyInt());

        assertThrows(IOException.class, () -> {
            ftpService.uploadFileToFTP(server, port, user, pass, filePath, uploadPath);
        });

        verify(ftpClient, times(1)).connect(server, port);
        verify(ftpClient, times(1)).login(user, pass);
        verify(ftpClient, times(1)).enterLocalPassiveMode();
        verify(ftpClient, times(1)).setFileType(FTP.BINARY_FILE_TYPE);
        verify(ftpClient, times(1)).storeFile(eq(uploadPath), any(InputStream.class));

        // Delete the temporary file after the test
        Files.deleteIfExists(tempFilePath);
    }
}

