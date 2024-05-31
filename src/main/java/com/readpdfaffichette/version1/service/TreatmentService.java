/*
 * Nom         : Treatement.java
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
import java.util.stream.Stream;

import com.readpdfaffichette.version1.exceptions.CustomAppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class TreatmentService {

    private final PdfService pdfService;
    private final FilesService filesService;
    private final RegexService regexService;
    /*private final FtpService ftpService;

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

     */

    public TreatmentService(PdfService pdfService, FilesService filesService, RegexService regexService) {
        this.pdfService = pdfService;
        this.filesService = filesService;
        this.regexService = regexService;
        //this.ftpService = ftpService;
    }

    public void readpdf(String[] args) throws IOException, CustomAppException {

        // Dossier contenant les fichiers PDF
        File folder = new File(filesService.getLink());
        if (!folder.isDirectory()) {
            System.out.println("Erreur : Le chemin n'amène pas à un dossier.");
            return;
        }

        // Copie de la partie1 du doc HTML, afin de pouvoir le modifier ensuite
        filesService.copyFile(filesService.getTextFile1(), filesService.getTextFile1Saveplace());

        // Fichier texte de sortie
        try (Stream<Path> paths = Files.walk(Paths.get(filesService.getLink()))) {
            StringBuilder allTexts = pdfService.processPdfs(paths, regexService);

            // Ajouter le texte extrait à la fin du fichier texte existant (partie1 : début du code HTML)
            filesService.writeFile(filesService.getTextFile1Saveplace(), allTexts.toString());
        }

        // Merger les deux fichiers txt qui serviront à la page HTML
        filesService.mergeTextFiles(filesService.getTextFile1Saveplace(), filesService.getTextFile3(), filesService.getMergedFile());

        // Supprimer la partie1 du dossier
        filesService.deleteFile(filesService.getTextFile1Saveplace());

    }

    /*private void uploadFileToFTP(String filePath) throws IOException {
        ftpService.uploadFileToFTP(ftpServer, ftpPort, ftpUser, ftpPassword, filePath, ftpUploadPath);
    }*/
}


