/*
 * Nom         : reggex.java
 *
 * Description : Classe permettant de gérer les reggex lié au tri du texte extrait du pdf.
 *
 * Date        : 23/05/2024
 * 
 */

 package com.readpdfaffichette.version1.classes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class reggex {

    //récupération des reggex
    @Value("${reggex.titles}")
    private String reggexTitles;

    @Value("${reggex.city}")
    private String reggexCity;

    @Value("${reggex.date}")
    private String reggexDate;

    //getters et setters
    public String getReggexTitles() {
        return reggexTitles;
    }

    public void setReggexTitles(String reggexTitles) {
        this.reggexTitles = reggexTitles;
    }

    public String getReggexCity() {
        return reggexCity;
    }

    public void setReggexCity(String reggexCity) {
        this.reggexCity = reggexCity;
    }

    public String getReggexDate() {
        return reggexDate;
    }

    public void setReggexDate(String reggexDate) {
        this.reggexDate = reggexDate;
    }

    //méthodes 
    
    //méthode permettant d'extraire du texte brut les titres et des les séparer si il y en a 2
    public static String[] extractTitles(String text, reggex reggex) {
        // Regex pour les Titres
        String titlesRegex = reggex.getReggexTitles();
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
        return new String[] {"NoTitle"};
    }

    //méthode permettant d'extraire du texte brut la ville et le code postal
    public static String extractCityAndPostalCode(String text, reggex reggex) {
        // Regex pour la Ville et le Code Postal
        String cityAndPostalCodeRegex = reggex.getReggexCity();
        Pattern cityAndPostalCodePattern = Pattern.compile(cityAndPostalCodeRegex);
        Matcher cityAndPostalCodeMatcher = cityAndPostalCodePattern.matcher(text);
        return cityAndPostalCodeMatcher.find() ? cityAndPostalCodeMatcher.group().trim() : "null";
    }

    //méthode permettant d'extraire du texte brut la date
    public static String extractDate(String text, reggex reggex) {
        // Regex pour la Date
        String dateRegex = reggex.getReggexDate();
        Pattern datePattern = Pattern.compile(dateRegex);
        Matcher dateMatcher = datePattern.matcher(text);
        return dateMatcher.find() ? dateMatcher.group().trim() : "null";
    }
}
