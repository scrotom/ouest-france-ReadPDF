/*
 * Nom         : FtpService.java
 *
 * Description : Classe permettant d'envoyer des fichiers sur un serveur distant avec un protocole ftp.
 *
 * Date        : 31/05/2024
 *
 */

package com.readpdfaffichette.version1.service;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Service;

import java.io.File;
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
            logger.info("Connexion au serveur FTP...");
            ftpClient.connect(server, port);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                throw new IOException("impossible de se connecter au serveur FTP " + replyCode);
            }
            logger.info("connecté au serveur FTP");

            boolean success = ftpClient.login(user, pass);
            if (!success) {
                throw new IOException("nom d'utilisateur ou mot de passe incorrect pour le serveur FTP");
            }
            logger.info("mot de passe et nom d'utilisateur correct : connecté au serveur FTP");

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                throw new IOException("Fichier à téléverser introuvable : " + filePath);
            }

            try (InputStream inputStream = new FileInputStream(filePath)) {
                logger.info("envoi du fichier...");
                boolean done = ftpClient.storeFile(uploadPath, inputStream);
                logger.info("Réponse FTP après tentative de téléversement : " + ftpClient.getReplyString());
                if (!done) {
                    throw new IOException("impossible d'envoyer le fichier vers le serveur FTP");
                }
                logger.info("envoie du fichier reussi");
            }
            ftpClient.logout();
            logger.info("déconnexion du serveur FTP");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "erreur lors de l'envoi du fichier " + ex.getMessage(), ex);
            throw new IOException("erreur lors de l'envoi du fichier " + ex.getMessage(), ex);
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                    logger.info("déconnecté du serveur");
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "erreur lors de la déconnexion du serveur " + ex.getMessage(), ex);
                }
            }
        }
    }
}
