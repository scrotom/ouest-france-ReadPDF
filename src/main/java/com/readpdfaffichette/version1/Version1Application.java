package com.readpdfaffichette.version1;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
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

        //copie du fichier css
        Files.copy(fileLocation.getStyleCssSource(), fileLocation.getStyleCssSaveplace(), StandardCopyOption.REPLACE_EXISTING);

        //copie de la partie1 du doc html, afin de pouvoir le modifier ensuite
        Files.copy(fileLocation.getTextFile1(), fileLocation.getTextFile1Saveplace(), StandardCopyOption.REPLACE_EXISTING);

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

        // ajouter le texte extrait à la fin du fichier texte existant (partie1 : début du code html)
        Files.write(fileLocation.getTextFile1Saveplace(), allTexts.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);

        //merger les deux fichier txt qui serviront à la page html
        mergeTextFiles(fileLocation.getTextFile1Saveplace(), fileLocation.getTextFile3(), fileLocation.getMergedFile());

        //supprimer la partie1 du dossier final
        Files.delete(fileLocation.getTextFile1Saveplace());
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
        System.err.println(text);

        String sortedText2 = "<tr><th>" + date + "</th><th>" + titles + "</th><th>" + cityAndPostalCode + "</th></tr>";
        
        return sortedText2;
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

    public static void mergeTextFiles(Path file1Path, Path file2Path, Path combinedFilePath) throws IOException {
        // Lire le contenu des deux fichiers
        String file1Content = Files.readString(file1Path, StandardCharsets.UTF_8);
        String file2Content = Files.readString(file2Path, StandardCharsets.UTF_8);

        // Combiner les contenus
        String combinedContent = file1Content + "\n" + file2Content;

        // Écrire le contenu combiné dans un nouveau fichier
        Files.write(combinedFilePath, combinedContent.getBytes(StandardCharsets.UTF_8));
    }
    
}
