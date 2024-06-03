/*
 * Nom         : TreatementService.java
 *
 * Description : Classe permettant de réaliser le traitement des fichiers pdf afin d'extraire leurs données et de les concaténer dans un doc html.
 *
 * Date        : 23/05/2024
 *
 */

package com.readpdfaffichette.version1.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import com.readpdfaffichette.version1.exceptions.CustomAppException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class TreatmentService {

    private final PdfService pdfService;
    private final FilesService filesService;
    private final RegexService regexService;
    private final FtpService ftpService;

    @Value("${ftp.server}")
    private String ftpServer;

    @Value("${ftp.port}")
    private int ftpPort;

    @Value("${ftp.user}")
    private String ftpUser;

    @Value("${ftp.password}")
    private String ftpPassword;

    @Value("${ftp.upload.path}")
    private String ftpUploadPath;

    public TreatmentService(PdfService pdfService, FilesService filesService, RegexService regexService, FtpService ftpService) {
        this.pdfService = pdfService;
        this.filesService = filesService;
        this.regexService = regexService;
        this.ftpService = ftpService;
    }

    public void readpdf(String[] args) throws IOException, CustomAppException {
        log.info("Démarrage du traitement des fichiers PDF");
        // Dossier contenant les fichiers PDF
        File folder = new File(filesService.getLink());
        if (!folder.isDirectory()) {
            log.error("Erreur : Le chemin n'amène pas à un dossier. Chemin: {}", folder.getAbsolutePath());
            return;
        }

        log.info("Copie du fichier partie1 pour modification");
        // Copie de la partie1 du doc HTML, afin de pouvoir le modifier ensuite
        filesService.copyFile(filesService.getTextFile1(), filesService.getTextFile1Saveplace());

        log.info("Traitement des fichiers PDF dans le dossier");
        // Fichier texte de sortie
        try (Stream<Path> paths = Files.walk(Paths.get(filesService.getLink()))) {
            StringBuilder allTexts = pdfService.processPdfs(paths, regexService);

            log.info("Ajout du texte extrait au fichier texte existant");
            // Ajouter le texte extrait à la fin du fichier texte existant (partie1 : début du code HTML)
            filesService.writeFile(filesService.getTextFile1Saveplace(), allTexts.toString());
        }

        // Obtenir le chemin du fichier de sortie avec la date actuelle
        Path mergedFilePath = filesService.getMergedFilePath();

        log.info("Fusion des fichiers texte pour créer le fichier HTML final");
        // Merger les deux fichiers txt qui serviront à la page HTML
        filesService.mergeTextFiles(filesService.getTextFile1Saveplace(), filesService.getTextFile3(), mergedFilePath);

        log.info("Suppression du fichier temporaire partie1");
        // Supprimer la partie1 du dossier
        filesService.deleteFile(filesService.getTextFile1Saveplace());

        //upload sur le serveur ftp
        log.info("Envoi du fichier HTML sur le serveur FTP");
        uploadFileToFTP(mergedFilePath.toString());

        log.info("Traitement des fichiers PDF terminé");

    }

    private void uploadFileToFTP(String filePath) throws IOException {
        // Générer un chemin de fichier FTP dynamique avec la date du jour actuel
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dynamicFtpUploadPath = ftpUploadPath.replace(".html", "_" + dateStr + ".html");

        ftpService.uploadFileToFTP(ftpServer, ftpPort, ftpUser, ftpPassword, filePath, dynamicFtpUploadPath);
    }
}



