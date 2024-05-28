/*
 * Nom         : RegexUtil.java
 *
 * Description : Classe permettant de gérer les regex lié au tri du texte extrait du pdf.
 *
 * Date        : 23/05/2024
 * 
 */

 package com.readpdfaffichette.version1.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.readpdfaffichette.version1.exceptions.CustomAppException;

@Component

public class RegexUtil {

    //récupération des regex
    @Value("${regex.titles}")
    private String regexTitles;

    @Value("${regex.city}")
    private String regexCity;

    @Value("${regex.date}")
    private String regexDate;

    //getters et setters

    public String getRegexTitles() {
        return regexTitles;
    }

    public void setRegexTitles(String regexTitles) {
        this.regexTitles = regexTitles;
    }

    public String getRegexCity() {
        return regexCity;
    }

    public void setRegexCity(String regexCity) {
        this.regexCity = regexCity;
    }

    public String getRegexDate() {
        return regexDate;
    }

    public void setRegexDate(String regexDate) {
        this.regexDate = regexDate;
    }

    //méthodes 
    
    //méthode permettant d'extraire du texte brut les titres et des les séparer si il y en a 2
    public String[] extractTitles(String text) throws CustomAppException {
        try {
            // Regex pour les Titres
            String titlesRegex = getRegexTitles();
            Pattern titlesPattern = Pattern.compile(titlesRegex, Pattern.DOTALL);
            Matcher titlesMatcher = titlesPattern.matcher(text);
        
            if (titlesMatcher.find()) {
                String titles = titlesMatcher.group(1).trim();

                // Vérifier s'il y a un deuxième point pour séparer les titres
                int firstDotIndex = titles.indexOf('.');
                int secondDotIndex = titles.indexOf('.', firstDotIndex + 1);

                if (secondDotIndex != -1) {
                    // Il y a un deuxième point, séparer les titres
                    String firstTitleLine = titles.substring(0, titles.lastIndexOf('\n', secondDotIndex) + 1).trim();
                    String secondTitleLine = titles.substring(titles.lastIndexOf('\n', secondDotIndex) + 1).trim();
    
                    // Extraire les sujets
                    String firstTitleSubject = firstTitleLine.substring(0, firstTitleLine.indexOf('.') + 1).trim();
                    String secondTitleSubject = secondTitleLine.substring(0, secondTitleLine.indexOf('.') + 1).trim();
    
                   // Retirer les sujets des titres
                    String firstTitle = firstTitleLine.substring(firstTitleSubject.length()).trim();
                    String secondTitle = secondTitleLine.substring(secondTitleSubject.length()).trim();

                    return new String[] { firstTitleSubject, firstTitle, secondTitleSubject, secondTitle };
                }

            // Retourner le titre complet si un seul titre est présent
            String titleSubject = titles.substring(0, titles.indexOf('.') + 1).trim();
            String title = titles.substring(titleSubject.length()).trim();
            return new String[] { titleSubject, title};
            }
            return new String[] {"pas de titre"};

        }catch (Exception e){
            throw new CustomAppException("erreur lors de l'extraction du(des) titres par le regex : " +e.getMessage());
        }

    }

    //méthode permettant d'extraire du texte brut la ville et le code postal
    public String extractCityAndPostalCode(String text) throws CustomAppException{
        try {
        // Regex pour la Ville et le Code Postal
        String cityAndPostalCodeRegex = getRegexCity();
        Pattern cityAndPostalCodePattern = Pattern.compile(cityAndPostalCodeRegex);
        Matcher cityAndPostalCodeMatcher = cityAndPostalCodePattern.matcher(text);
        return cityAndPostalCodeMatcher.find() ? cityAndPostalCodeMatcher.group().trim() : "pas de ville";
        } catch (Exception e){
            throw new CustomAppException("erreur lors de l'extraction de la ville par le regex : " +e.getMessage());
        }
    }

    //méthode permettant d'extraire du texte brut la date
    public String extractDate(String text) throws CustomAppException{
        try{
        // Regex pour la Date
        String dateRegex = getRegexDate();
        Pattern datePattern = Pattern.compile(dateRegex);
        Matcher dateMatcher = datePattern.matcher(text);
        return dateMatcher.find() ? dateMatcher.group().trim() : "pas de date";
        } catch (Exception e) {
            throw new CustomAppException("erreur lors de l'extraction de la date par le regex : " +e.getMessage());
        }

    }
}
