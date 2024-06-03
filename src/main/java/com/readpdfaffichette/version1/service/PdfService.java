/*
 * Nom         : PdfService.java
 *
 * Description : Classe permettant de gérer les méthodes liés au scan des pdf, et a la mise en page du doc html final
 *
 * Date        : 23/05/2024
 *
 */

package com.readpdfaffichette.version1.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import com.readpdfaffichette.version1.exceptions.CustomAppException;

@Component
@Log4j2
public class PdfService {

    private final RegexService regexService;

    public PdfService(RegexService regexService) {
        this.regexService = regexService;
    }

    //méthode permettant d'extraire le texte des pdf
    public String extractTextFromPDF(File file, RegexService regex) throws CustomAppException {
        log.info("Extraction du texte depuis le fichier PDF : {}", file.getName());
        String text;
        // Instancier la classe PDFTextStripper
        try (PDDocument document = PDDocument.load(file)) {
            // Instancier la classe PDFTextStripper
            PDFTextStripper pdfStripper = new PDFTextStripper();
            // Extraire le texte du document PDF
            text = pdfStripper.getText(document);
            log.info("Texte extrait avec succès du fichier PDF : {}", file.getName());
            // Fermer le document PDF
            return text;
        } catch (IOException e) {
            log.error("Erreur lors de l'extraction du texte depuis le fichier PDF : {}", e.getMessage());
            throw new CustomAppException("erreur lors de l'extraction du texte depuis le pdf : " + e.getMessage());
        }
    }

    //méthode permettant de trier le texte extrait des pdf
    public String sortText (String text, RegexService regex) throws CustomAppException {
        log.info("Tri du texte extrait");
        try {
        // Séparer les différentes parties du texte
        String[] titles = regex.extractTitles(text);
        String titles1 = titles[1];
        String titles1Subject = titles[0];
        String titles2 = titles.length > 2 ? titles[3] : "<br>";
        String titles2Subject = titles.length > 2 ? titles[2] : "";
        String cityAndPostalCode = regex.extractCityAndPostalCode(text);
        String date = regex.extractDate(text);

        // Concaténer les informations triées
        String sortedText ="<TR><TD class=\"tableauAffichette\" width=\"25%\">" + cityAndPostalCode + "</TD><TD class=\"tableauAffichette\" width=\"25%\">" + date + "</TD><TD class=\"tableauAffichette\"><u>" + titles1Subject + "</u> " + titles1 + "<BR><u>" + titles2Subject + "</u> " + titles2 + "</TD></TR>";
        log.info("Texte trié avec succès");
        return sortedText;
        } catch (Exception e) {
            log.error("Erreur lors du tri du texte extrait : {}", e.getMessage());
            throw new CustomAppException("erreur lors du tri du texte extrait : " +e.getMessage());
        }
    }

    // méthode permettant de traiter les PDF dans un dossier
    public StringBuilder processPdfs(Stream<Path> paths, RegexService regex) throws CustomAppException {
        log.info("Traitement des fichiers PDF dans le dossier");
        StringBuilder allTexts = new StringBuilder();
        List<Path> pathList = paths.filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".pdf"))
                .toList();

        for (Path path : pathList) {
            try {
                String text = extractTextFromPDF(path.toFile(), regex);
                text = sortText(text, regex);
                allTexts.append(text).append("\n\n");
                log.info("Fichier PDF traité : {}", path.getFileName());
            } catch (CustomAppException e) {
                log.error("Erreur lors du traitement du fichier PDF : {}", path.getFileName(), e);
                e.printStackTrace(System.out);
            }
        }

        log.info("Traitement des fichiers PDF terminé");
        return allTexts;
    }
}

