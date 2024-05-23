package com.readpdfaffichette.version1.classes;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

@Component
public class pdfExtraction {

    //méthodes 
    
    //méthode permettant d'extraire le texte des pdf, et de le trier pour le document html
    public static String extractTextFromPDF(File file, reggex Reggex) throws IOException {

        String text;
        // Instancier la classe PDFTextStripper
        try (PDDocument document = PDDocument.load(file)) {
            // Instancier la classe PDFTextStripper
            PDFTextStripper pdfStripper = new PDFTextStripper();
            // Extraire le texte du document PDF
            text = pdfStripper.getText(document);
            // Fermer le document PDF
        }

        // Séparer les différentes parties du texte
        String[] titles = reggex.extractTitles(text, Reggex);
        String titles1 = titles[0];
        String titles2 = titles.length > 1 ? titles[1] : "<br>";
        String cityAndPostalCode = reggex.extractCityAndPostalCode(text, Reggex);
        String date = reggex.extractDate(text, Reggex);

        // Concaténer les informations triées
        String sortedText ="<TR><TD class=\"tableauAffichette\" width=\"25%\">" + cityAndPostalCode + "</TD><TD class=\"tableauAffichette\" width=\"25%\">" + date + "</TD><TD class=\"tableauAffichette\">" + titles1 + "<BR>" + titles2 + "</TD></TR>";
        
        return sortedText;
    }  
}
