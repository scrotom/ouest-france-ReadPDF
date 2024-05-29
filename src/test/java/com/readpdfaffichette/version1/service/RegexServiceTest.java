package com.readpdfaffichette.version1.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import com.readpdfaffichette.version1.exceptions.CustomAppException;

public class RegexServiceTest {

    @InjectMocks
    private RegexService regexService;

    @Value("${regex.titles}")
    private String regexTitles = "(.*?)(?=(?:PHOTO :|\\d{2} -))";

    @Value("${regex.city}")
    private String regexCity = "\\d{2} - .+";

    @Value("${regex.date}")
    private String regexDate = "([a-zA-Z]+ \\d{1,2} \\w+ \\d{4})";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        regexService.setRegexTitles(regexTitles);
        regexService.setRegexCity(regexCity);
        regexService.setRegexDate(regexDate);
    }

    @Test
    @DisplayName("test pour vérifier si la méthode ExtractTitles extrait bien les titres présent dans un texte si il y a 2 titres")
    public void testExtractTitlesSuccessWith2title() throws CustomAppException {
        String text = " Title1. \r\n Subtitle1 \r\n Title2. \r\n Subtitle2 54 -";
        String[] expectedTitles = { "Title1.", "Subtitle1", "Title2.", "Subtitle2" };
        String[] extractedTitles = regexService.extractTitles(text);

        assertArrayEquals(expectedTitles, extractedTitles);
    }

    @Test
    @DisplayName("test pour vérifier si la méthode ExtractTitles extrait bien le titre présent dans un texte si il y a 1 titre ")
    public void testExtractTitlesSuccessWith1title() throws CustomAppException {
        String text = " Title1. \r\n Subtitle1 54 -";
        String[] expectedTitles = { "Title1.", "Subtitle1"};
        String[] extractedTitles = regexService.extractTitles(text);

        assertArrayEquals(expectedTitles, extractedTitles);
    }

    @Test
    @DisplayName("test pour vérifier si la méthode ExtractTitles renvoie bien une erreur si il n'y a pas de titre")
    public void testExtractTitlesFailure() {
        String text = "";

        assertThrows(CustomAppException.class, () -> regexService.extractTitles(text));
    }

    @Test
    @DisplayName("test pour vérifier si la méthode ExtractCityAndPostalCode renvoie bien la ville et le code postal présent dans un texte")
    public void testExtractCityAndPostalCodeSuccess() throws CustomAppException {
        String text = "56 - CityName";
        String expectedCityAndPostalCode = "56 - CityName";
        String extractedCityAndPostalCode = regexService.extractCityAndPostalCode(text);

        assertEquals(expectedCityAndPostalCode, extractedCityAndPostalCode);
    }

    @Test
    @DisplayName("test pour vérifier si la méthode ExtractCityAndPostalCode renvoie bien une erreur si il n'y a pas de ville et de code postal")
    public void testExtractCityAndPostalCodeFailure() {
        String text = "";

        assertThrows(CustomAppException.class, () -> regexService.extractCityAndPostalCode(text));
    }

    @Test
    @DisplayName("test pour vérifier si la méthode ExtractDate renvoie bien la date présente dans un texte")
    public void testExtractDateSuccess() throws CustomAppException {
        String text = "vendredi 21 mai 2024";
        String expectedDate = "vendredi 21 mai 2024";
        String extractedDate = regexService.extractDate(text);

        assertEquals(expectedDate, extractedDate);
    }

    @Test
    @DisplayName("test pour vérifier si la méthode ExtractDate renvoie bien une erreur si il n'y a pas de date ")
    public void testExtractDateFailure() {
        String text = "";

        assertThrows(CustomAppException.class, () -> regexService.extractDate(text));
    }
}

