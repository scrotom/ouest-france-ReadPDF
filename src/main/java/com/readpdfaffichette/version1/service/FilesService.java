/*
 * Nom         : FilesService.java
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lombok.Getter;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.readpdfaffichette.version1.exceptions.CustomAppException;


@Component
@Getter
@Log4j2
public class FilesService {

    @Value("${inputRepository.path}")
    private String link;

    @Value("${partie1.inputPath}")
    private Path textFile1;

    @Value("${partie3.inputPath}")
    private Path textFile3;

    @Value("${partie1.outputPath}")
    private Path textFile1Saveplace;

    @Value("${mergedFile.basePath}")
    private String mergedFileBasePath;

    //METHODES

    // méthode permettant d'ajouter la date à la fin du nom de fichier
    public Path getMergedFilePath() {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Path mergedFilePath = Path.of(mergedFileBasePath + "_" + dateStr + ".html");
        log.info("Chemin du fichier combiné généré : {}", mergedFilePath);
        return mergedFilePath;
    }

    // méthode permettant d'obtenir la date d'hier au format "yyyy-MM-dd"
    private String getYesterdayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);  // réduire un jour
        return sdf.format(calendar.getTime());
    }

    // méthode permettant de supprimer le fichier HTML du jour précédent
    public void deleteYesterdayFile() throws IOException {
        String yesterdayDate = getYesterdayDate();
        Path yesterdayFilePath = Path.of(mergedFileBasePath + "_" + yesterdayDate + ".html");
        if (Files.exists(yesterdayFilePath)) {
            log.info("Suppression du fichier de log de la veille : {}", yesterdayFilePath);
            Files.delete(yesterdayFilePath);
        } else {
            log.info("Aucun fichier de log trouvé pour la veille : {}", yesterdayFilePath);
        }
    }


    //méthode permettant de supprimer un fichier
    public void deleteFile(Path filePath) throws IOException {
        log.info("Suppression du fichier : {}", filePath);
        Files.delete(filePath);
    }

    //méthode permettant de copier un fichier d'un endroit a un autre
    public void copyFile(Path source, Path destination) throws IOException {
        log.info("Copie du fichier de {} vers {}", source, destination);
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    //méthode permettant d'écrire dans un fichier
    public void writeFile(Path filePath, String content) throws IOException {
        log.info("Écriture dans le fichier : {}", filePath);
        Files.writeString(filePath, content, StandardOpenOption.APPEND);
    }

    //méthode permettant de combiner deux fichier texte
    public void mergeTextFiles(Path file1Path, Path file2Path, Path combinedFilePath) throws IOException, CustomAppException {
        log.info("Fusion des fichiers {} et {} dans {}", file1Path, file2Path, combinedFilePath);
        try {
            // Lire le contenu des deux fichiers
            String file1Content = Files.readString(file1Path, StandardCharsets.UTF_8);
            String file2Content = Files.readString(file2Path, StandardCharsets.UTF_8);

            // Combiner les contenus
            String combinedContent = file1Content + file2Content;

            // Écrire le contenu combiné dans un nouveau fichier
            Files.write(combinedFilePath, combinedContent.getBytes(StandardCharsets.UTF_8));
            log.info("Fusion réussie des fichiers dans : {}", combinedFilePath);
        } catch (IOException e) {
            log.error("Erreur lors de l'assemblage des fichiers : {}", e.getMessage());
            throw new CustomAppException("erreur lors de l'assemblage des deux fichiers : " + e.getMessage());
        }
    }
}

