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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.readpdfaffichette.version1.parametres.ressources;

@SpringBootApplication
public class Version1Application {

    public static void main(String[] args) throws IOException {

        var context = SpringApplication.run(Version1Application.class, args);
        ressources ressources = context.getBean(ressources.class);

        // Dossier contenant les fichiers PDF
        File folder = new File(ressources.getLink());
        if (!folder.isDirectory()) {
            System.out.println("erreur : Le chemin n'ammène pas à un dossier.");
            return;
        }

        //copie du fichier css
        Files.copy(ressources.getStyleCssSource(), ressources.getStyleCssSaveplace(), StandardCopyOption.REPLACE_EXISTING);

        //copie de la partie1 du doc html, afin de pouvoir le modifier ensuite
        Files.copy(ressources.getTextFile1(), ressources.getTextFile1Saveplace(), StandardCopyOption.REPLACE_EXISTING);

        // Fichier texte de sortie
        StringBuilder allTexts = new StringBuilder();

        // parcours tout les fichiers du dossier a partir de l'endroit spécifié par ressources (transformé en path).
        try (Stream<Path> paths = Files.walk(Paths.get(ressources.getLink()))) {

            //permet de ne garder que les fichiers en .pdf
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".pdf"))
                 
                 //pour chaque chemin obtenu, permet de le transformer en fichier et d'extraire le texte 
                 .forEach(path -> {
                     try {
                         String text = extractTextFromPDF(path.toFile(), ressources);
                         allTexts.append(text).append("\n\n");
                     } catch (IOException exception) {
                        //warning sans le system.out ??
                         exception.printStackTrace(System.out);
                     }
                 });
        }

        // ajouter le texte extrait à la fin du fichier texte existant (partie1 : début du code html)
        Files.write(ressources.getTextFile1Saveplace(), allTexts.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);

        //merger les deux fichier txt qui serviront à la page html
        mergeTextFiles(ressources.getTextFile1Saveplace(), ressources.getTextFile3(), ressources.getMergedFile());

        //supprimer la partie1 du dossier final
        Files.delete(ressources.getTextFile1Saveplace());
    }
    
    //méthode permettant d'extraire le texte des pdf, et de le trier pour le document html
    public static String extractTextFromPDF(File file, ressources ressources) throws IOException {

        PDDocument document = PDDocument.load(file);

        // Instancier la classe PDFTextStripper
        PDFTextStripper pdfStripper = new PDFTextStripper();

        // Extraire le texte du document PDF
        String text = pdfStripper.getText(document);

        // Fermer le document PDF
        document.close();

        // Séparer les différentes parties du texte
        String[] titles = extractTitles(text, ressources);
        String titles1 = titles[0];
        String titles2 = titles.length > 1 ? titles[1] : "pas de deuxième titre";
        String cityAndPostalCode = extractCityAndPostalCode(text, ressources);
        String date = extractDate(text, ressources);

        // Concaténer les informations triées
        String sortedText = "<tr><th>" + date + "</th><th>" + titles1 + "</th><th>" + titles2 + "</th><th>"+ cityAndPostalCode + "</th></tr>";
       
        return sortedText;
    }

    //méthode permettant d'extraire du texte brut les titres et des les séparer si il y en a 2
    public static String[] extractTitles(String text, ressources ressources) {
        // Regex pour les Titres
        String titlesRegex = ressources.getReggexTitles();
        Pattern titlesPattern = Pattern.compile(titlesRegex, Pattern.DOTALL);
        Matcher titlesMatcher = titlesPattern.matcher(text);
        
        if (titlesMatcher.find()) {
            String titles = titlesMatcher.group(1).trim();

            // Vérifier s'il y a un deuxième point pour séparer les titres
            int firstDotIndex = titles.indexOf('.');
            int secondDotIndex = titles.indexOf('.', firstDotIndex + 1);

            if (secondDotIndex != -1) {
                // Il y a un deuxième point, séparer les titres
                String firstTitle = titles.substring(0, titles.lastIndexOf('\n', secondDotIndex) + 1).trim();
                String secondTitle = titles.substring(titles.lastIndexOf('\n', secondDotIndex) + 1).trim();
                return new String[] { firstTitle, secondTitle };
            }

            // Retourner le titre complet si un seul titre est présent
            return new String[] { titles };
        }
        return new String[] { "null" };
    }

    //méthode permettant d'extraire du texte brut la ville et le code postal
    public static String extractCityAndPostalCode(String text, ressources ressources) {
        // Regex pour la Ville et le Code Postal
        String cityAndPostalCodeRegex = ressources.getReggexCity();
        Pattern cityAndPostalCodePattern = Pattern.compile(cityAndPostalCodeRegex);
        Matcher cityAndPostalCodeMatcher = cityAndPostalCodePattern.matcher(text);
        return cityAndPostalCodeMatcher.find() ? cityAndPostalCodeMatcher.group().trim() : "null";
    }

    //méthode permettant d'extraire du texte brut la date
    public static String extractDate(String text, ressources ressources) {
        // Regex pour la Date
        String dateRegex = ressources.getReggexDate();
        Pattern datePattern = Pattern.compile(dateRegex);
        Matcher dateMatcher = datePattern.matcher(text);
        return dateMatcher.find() ? dateMatcher.group().trim() : "null";
    }

    //méthode permettant de combiner deux fichier texte
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
