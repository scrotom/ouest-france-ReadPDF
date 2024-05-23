package com.readpdfaffichette.version1.classes;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

@Component
public class pdfExtraction {
    
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
        String titles2 = titles.length > 1 ? titles[1] : "pas de deuxième titre";
        String cityAndPostalCode = reggex.extractCityAndPostalCode(text, Reggex);
        String date = reggex.extractDate(text, Reggex);

        // Concaténer les informations triées
        String sortedText = "<tr><th>" + date + "</th><th>" + titles1 + "</th><th>" + titles2 + "</th><th>"+ cityAndPostalCode + "</th></tr>";
       
        return sortedText;
    }  
}
