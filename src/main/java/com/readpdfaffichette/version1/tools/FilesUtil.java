/*
 * Nom         : FilesUtil.java
 *
 * Description : Classe permettant la gestion des fichiers lié à l'application
 *
 * Date        : 23/05/2024
 * 
 */
package com.readpdfaffichette.version1.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.readpdfaffichette.version1.exceptions.CustomAppException;

@Component

public class FilesUtil {

    //récupératon du chemin vers le dossier ou sont placé les pdf
    @Value("${inputRepository.path}")
    private String link;
    
    //chemin pour le fichier css
    @Value("${styleCss.outputPath}")
    private Path styleCssSaveplace;

    @Value("${styleCss.inputPath}")
    private Path styleCssSource;

    //récupération des fichiers pour le doc html final
    @Value("${partie1.inputPath}")
    private Path textFile1;

    @Value("${partie3.inputPath}")
    private Path textFile3;

    @Value("${partie1.outputPath}")
    private Path textFile1Saveplace;

    //récupération du dossier d'enregistrement des document mergé
    @Value("${mergedFile.outputPath}")
    private Path mergedFile;

    //getter

    public String getLink() {
        return link;
    }

    public Path getStyleCssSaveplace() {
        return styleCssSaveplace;
    }

    public Path getStyleCssSource() {
        return styleCssSource;
    }

    public Path getTextFile1() {
        return textFile1;
    }

    public Path getTextFile3() {
        return textFile3;
    }

    public Path getMergedFile() {
        return mergedFile;
    }

    public Path getTextFile1Saveplace() {
        return textFile1Saveplace;
    }

    //méthodes
    
    //méthode permettant de combiner deux fichier texte
    public static void mergeTextFiles(Path file1Path, Path file2Path, Path combinedFilePath) throws IOException, CustomAppException {
        try {
        // Lire le contenu des deux fichiers
        String file1Content = Files.readString(file1Path, StandardCharsets.UTF_8);
        String file2Content = Files.readString(file2Path, StandardCharsets.UTF_8);

        // Combiner les contenus
        String combinedContent = file1Content + file2Content;

        // Écrire le contenu combiné dans un nouveau fichier
        Files.write(combinedFilePath, combinedContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new CustomAppException("erreur lors de l'assemblage des deux fichiers : " + e.getMessage());
        }
    }
}

