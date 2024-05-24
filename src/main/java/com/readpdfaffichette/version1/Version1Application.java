/*
 * Nom         : Version1Application.java
 *
 * Description : Application permettant d'effectuer les opérations dans le but de scanner des affichettes au format pdf, et d'en extraire le contenu trié dans un doc html.
 *
 * Date        : 23/05/2024
 * 
 */

 package com.readpdfaffichette.version1;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.readpdfaffichette.version1.classes.filesInfo;
import com.readpdfaffichette.version1.classes.pdfExtraction;
import com.readpdfaffichette.version1.classes.reggex;

@SpringBootApplication
public class Version1Application {

    public static void main(String[] args) throws IOException {

        var context = SpringApplication.run(Version1Application.class, args);
        reggex Reggex = context.getBean(reggex.class);
        filesInfo Filesinfo = context.getBean(filesInfo.class);

        // Dossier contenant les fichiers PDF
        File folder = new File(Filesinfo.getLink());
        if (!folder.isDirectory()) {
            System.out.println("erreur : Le chemin n'ammène pas à un dossier.");
            return;
        }

        //copie du fichier css
        Files.copy(Filesinfo.getStyleCssSource(), Filesinfo.getStyleCssSaveplace(), StandardCopyOption.REPLACE_EXISTING);

        //copie de la partie1 du doc html, afin de pouvoir le modifier ensuite
        Files.copy(Filesinfo.getTextFile1(), Filesinfo.getTextFile1Saveplace(), StandardCopyOption.REPLACE_EXISTING);

        // Fichier texte de sortie
        StringBuilder allTexts = new StringBuilder();

        // parcours tout les fichiers du dossier a partir de l'endroit spécifié par ressources (transformé en path).
        try (Stream<Path> paths = Files.walk(Paths.get(Filesinfo.getLink()))) {

            //permet de ne garder que les fichiers en .pdf
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".pdf"))
                 
                 //pour chaque chemin obtenu, permet de le transformer en fichier et d'extraire le texte 
                 .forEach(path -> {
                     try {
                         String text = pdfExtraction.extractTextFromPDF(path.toFile(), Reggex);
                         allTexts.append(text).append("\n\n");
                     } catch (IOException exception) {
                         exception.printStackTrace(System.out);
                     }
                 });
        }

        // ajouter le texte extrait à la fin du fichier texte existant (partie1 : début du code html)
        Files.write(Filesinfo.getTextFile1Saveplace(), allTexts.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);

        //merger les deux fichier txt qui serviront à la page html
        filesInfo.mergeTextFiles(Filesinfo.getTextFile1Saveplace(), Filesinfo.getTextFile3(), Filesinfo.getMergedFile());

        //supprimer la partie1 du dossier final
        Files.delete(Filesinfo.getTextFile1Saveplace());
    } 
}
