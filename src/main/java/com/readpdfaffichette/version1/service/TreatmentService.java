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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import com.readpdfaffichette.version1.exceptions.CustomAppException;
import org.springframework.stereotype.Service;


@Service
public class TreatmentService {

    private final RegexService regex;
    private final FilesService file;
    private final PdfService pdfutil;

    public TreatmentService(RegexService regex, FilesService file, PdfService pdfutil) {
        this.regex = regex;
        this.file = file;
        this.pdfutil = pdfutil;
    }

    public void readpdf(String[] args) throws IOException, CustomAppException{

        // Dossier contenant les fichiers PDF
        File folder = new File(file.getLink());
        if (!folder.isDirectory()) {
            System.out.println("erreur : Le chemin n'ammène pas à un dossier.");
            return;
        }

        //copie du fichier css
        Files.copy(file.getStyleCssSource(), file.getStyleCssSaveplace(), StandardCopyOption.REPLACE_EXISTING);

        //copie de la partie1 du doc html, afin de pouvoir le modifier ensuite
        Files.copy(file.getTextFile1(), file.getTextFile1Saveplace(), StandardCopyOption.REPLACE_EXISTING);

        // Fichier texte de sortie
        StringBuilder allTexts = new StringBuilder();

        // parcours tout les fichiers du dossier a partir de l'endroit spécifié par ressources (transformé en path).
        try (Stream<Path> paths = Files.walk(Paths.get(file.getLink()))) {

            //permet de ne garder que les fichiers en .pdf
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".pdf"))
                 
                 //pour chaque chemin obtenu, permet de le transformer en fichier et d'extraire le texte 
                 .forEach(path -> {
                     try {
                         String text = pdfutil.extractTextFromPDF(path.toFile(), regex);
                         text = pdfutil.sortText(text, regex);
                         allTexts.append(text).append("\n\n");
                     } catch (CustomAppException exception) {
                         exception.printStackTrace(System.out);
                     }
                 });
        }

        // ajouter le texte extrait à la fin du fichier texte existant (partie1 : début du code html)
        Files.write(file.getTextFile1Saveplace(), allTexts.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);

        //merger les deux fichier txt qui serviront à la page html
        file.mergeTextFiles(file.getTextFile1Saveplace(), file.getTextFile3(), file.getMergedFile());

        //supprimer la partie1 du dossier final
        Files.delete(file.getTextFile1Saveplace());
    }     
}
