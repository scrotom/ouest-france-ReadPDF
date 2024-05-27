/*
 * Nom         : PdfUtil.java
 *
 * Description : Classe permettant de gérer les méthodes liés au scan des pdf, et a la mise en page du doc html final
 *
 * Date        : 23/05/2024
 * 
 */

package com.readpdfaffichette.version1.tools;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import com.readpdfaffichette.version1.exceptions.CustomAppException;

@Component
public class PdfUtil {
    
    //méthode permettant d'extraire le texte des pdf
    public static String extractTextFromPDF(File file, RegexUtil regex) throws CustomAppException {

        String text;
        // Instancier la classe PDFTextStripper
        try (PDDocument document = PDDocument.load(file)) {
            // Instancier la classe PDFTextStripper
            PDFTextStripper pdfStripper = new PDFTextStripper();
            // Extraire le texte du document PDF
            text = pdfStripper.getText(document);
            // Fermer le document PDF
            return text;
        } catch (IOException e) {
            throw new CustomAppException("erreur lors de l'extraction du texte depuis le pdf : " + e.getMessage());
        }
    }

    //méthode permettant de trier le texte extrait des pdf
    public static String sortText (String text, RegexUtil regex) throws CustomAppException {

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
                
        return sortedText;
        } catch (Exception e) {
            throw new CustomAppException("erreur lors du tri du texte extrait : " +e.getMessage());
        }
    }
}
