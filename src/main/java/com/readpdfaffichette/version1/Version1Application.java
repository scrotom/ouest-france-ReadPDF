package com.readpdfaffichette.version1;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.readpdfaffichette.version1.parametres.FileLocation;

@SpringBootApplication
public class Version1Application {

    public static void main(String[] args) throws IOException {

        var context = SpringApplication.run(Version1Application.class, args);
        FileLocation fileLocation = context.getBean(FileLocation.class);

        // Dossier contenant les fichiers PDF
        File folder = new File(fileLocation.getLink());
        if (!folder.isDirectory()) {
            System.out.println("erreur : Le chemin n'ammène pas à un dossier.");
            return;
        }

        // Fichier texte de sortie
        Path outputPath = Paths.get(fileLocation.getSaveplace());

        StringBuilder allTexts = new StringBuilder();

        // parcours tout les fichiers du dossier a partir de l'endroit spécifié par fileLocation (transformé en path).
        try (Stream<Path> paths = Files.walk(Paths.get(fileLocation.getLink()))) {

            //permet de ne garder que les fichiers en .pdf
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".pdf"))
                 
                 //pour chaque chemin obtenu, permet de le transformer en fichier et d'extraire le texte 
                 .forEach(path -> {
                     try {
                         String text = extractTextFromPDF(path.toFile(), fileLocation);
                         allTexts.append(text).append("\n\n");
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 });
        }

        // Écrire le texte extrait dans le fichier texte en utilisant UTF-8
        Files.write(outputPath, allTexts.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static String extractTextFromPDF(File file, FileLocation fileLocation) throws IOException {
        PDDocument document = PDDocument.load(file);

        // Instancier la classe PDFTextStripper
        PDFTextStripper pdfStripper = new PDFTextStripper();

        // Extraire le texte du document PDF
        String text = pdfStripper.getText(document);

        // Fermer le document PDF
        document.close();

        // Séparer les différentes parties du texte
        String titles = extractTitles(text, fileLocation);
        String cityAndPostalCode = extractCityAndPostalCode(text, fileLocation);
        String date = extractDate(text, fileLocation);

        // Concaténer les informations triées
        String sortedText = "Titre(s) : " + titles + "\n" +
                            "Ville et CP région : " + cityAndPostalCode + "\n" +
                            "Date : " + date;

        return sortedText;
    }

    public static String extractTitles(String text, FileLocation fileLocation) {
        // Regex pour les Titres
        String titlesRegex = fileLocation.getReggexTitles();
        Pattern titlesPattern = Pattern.compile(titlesRegex, Pattern.DOTALL);
        Matcher titlesMatcher = titlesPattern.matcher(text);
        return titlesMatcher.find() ? titlesMatcher.group(1).trim() : "null";
    }

    public static String extractCityAndPostalCode(String text, FileLocation fileLocation) {
        // Regex pour la Ville et le Code Postal
        String cityAndPostalCodeRegex = fileLocation.getReggexCity();
        Pattern cityAndPostalCodePattern = Pattern.compile(cityAndPostalCodeRegex);
        Matcher cityAndPostalCodeMatcher = cityAndPostalCodePattern.matcher(text);
        return cityAndPostalCodeMatcher.find() ? cityAndPostalCodeMatcher.group().trim() : "null";
    }

    public static String extractDate(String text, FileLocation filelocation) {
        // Regex pour la Date
        String dateRegex = filelocation.getReggexDate();
        Pattern datePattern = Pattern.compile(dateRegex);
        Matcher dateMatcher = datePattern.matcher(text);
        return dateMatcher.find() ? dateMatcher.group().trim() : "null";
    }
}
