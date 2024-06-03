/*
 * Nom         : RegexService.java
 *
 * Description : Classe permettant de gérer les regex lié au tri du texte extrait du pdf.
 *
 * Date        : 23/05/2024
 * 
 */

 package com.readpdfaffichette.version1.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.readpdfaffichette.version1.exceptions.CustomAppException;

@Component
@Getter
@Setter
@Log4j2
public class RegexService {

    //récupération des regex
    @Value("${regex.titles}")
    private String regexTitles;

    @Value("${regex.city}")
    private String regexCity;

    @Value("${regex.date}")
    private String regexDate;

    //méthodes 
    
    //méthode permettant d'extraire du texte brut les titres et des les séparer si il y en a 2
    public String[] extractTitles(String text) throws CustomAppException {
        log.info("Extraction des titres du texte");
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
                    log.info("Titres extraits avec succès");
                    return new String[] { firstTitleSubject, firstTitle, secondTitleSubject, secondTitle };
                }

                // Retourner le titre complet si un seul titre est présent
                String titleSubject = titles.substring(0, titles.indexOf('.') + 1).trim();
                String title = titles.substring(titleSubject.length()).trim();
                log.info("Titre unique extrait avec succès");
                return new String[] { titleSubject, title };
            } else {
                log.warn("Aucun titre trouvé dans le texte");
                throw new CustomAppException("Aucun titre trouvé dans le texte.");
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction des titres par le regex : {}", e.getMessage());
            throw new CustomAppException("Erreur lors de l'extraction des titres par le regex : " + e.getMessage());
        }
    }

    //méthode permettant d'extraire du texte brut la ville et le code postal
    public String extractCityAndPostalCode(String text) throws CustomAppException{
        log.info("Extraction de la ville et du code postal");
        try {
            // Regex pour la Ville et le Code Postal
            String cityAndPostalCodeRegex = getRegexCity();
            Pattern cityAndPostalCodePattern = Pattern.compile(cityAndPostalCodeRegex);
            Matcher cityAndPostalCodeMatcher = cityAndPostalCodePattern.matcher(text);
            if (cityAndPostalCodeMatcher.find()) {
                log.info("Ville et code postal extraits avec succès");
                return cityAndPostalCodeMatcher.group().trim();
            } else {
                log.warn("Aucune ville trouvée dans le texte");
                throw new CustomAppException("Aucune ville trouvée dans le texte.");
            }
        } catch (Exception e){
            log.error("Erreur lors de l'extraction de la ville par le regex : {}", e.getMessage());
            throw new CustomAppException("Erreur lors de l'extraction de la ville par le regex : " + e.getMessage());
        }
    }

    //méthode permettant d'extraire du texte brut la date
    public String extractDate(String text) throws CustomAppException {
        log.info("Extraction de la date");
        try {
            // Regex pour la Date
            String dateRegex = getRegexDate();
            Pattern datePattern = Pattern.compile(dateRegex);
            Matcher dateMatcher = datePattern.matcher(text);
            if (dateMatcher.find()) {
                log.info("Date extraite avec succès");
                return dateMatcher.group().trim();
            } else {
                log.warn("Aucune date trouvée dans le texte");
                throw new CustomAppException("Aucune date trouvée dans le texte.");
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction de la date par le regex : {}", e.getMessage());
            throw new CustomAppException("Erreur lors de l'extraction de la date par le regex : " + e.getMessage());
        }
    }
}
