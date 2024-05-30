/*
 * Nom         : FilesUtil.java
 *
 * Description : Classe permettant la gestion des fichiers lié à l'application
 *
 * Date        : 23/05/2024
 * 
 */
package com.readpdfaffichette.version1.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import lombok.Getter;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.readpdfaffichette.version1.exceptions.CustomAppException;

import javax.annotation.processing.Generated;

@Component
@Getter
public class FilesService {

    //récupératon du chemin vers le dossier ou sont placé les pdf
    @Value("${inputRepository.path}")
    private String link;

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

    //méthodes

    //méthode permettant de supprimer un fichier
    public void deleteFile(Path filePath) throws IOException {
        Files.delete(filePath);
    }

    //méthode permettant de copier un fichier d'un endroit a un autre
    public void copyFile(Path source, Path destination) throws IOException {
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    //méthode permettant d'écrire dans un fichier
    public void writeFile(Path filePath, String content) throws IOException {
        Files.writeString(filePath, content, StandardOpenOption.APPEND);
    }

    //méthode permettant de combiner deux fichier texte
    public void mergeTextFiles(Path file1Path, Path file2Path, Path combinedFilePath) throws IOException, CustomAppException {
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

