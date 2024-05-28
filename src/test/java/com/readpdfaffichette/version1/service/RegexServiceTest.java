package com.readpdfaffichette.version1.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
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
    public void testExtractTitlesSuccessWith2title() throws CustomAppException {
        String text = " Title1. \r\n Subtitle1 \r\n Title2. \r\n Subtitle2 54 -";
        String[] expectedTitles = { "Title1.", "Subtitle1", "Title2.", "Subtitle2" };
        String[] extractedTitles = regexService.extractTitles(text);

        assertArrayEquals(expectedTitles, extractedTitles);
    }

    @Test
    public void testExtractTitlesSuccessWith1title() throws CustomAppException {
        String text = " Title1. \r\n Subtitle1 54 -";
        String[] expectedTitles = { "Title1.", "Subtitle1"};
        String[] extractedTitles = regexService.extractTitles(text);

        assertArrayEquals(expectedTitles, extractedTitles);
    }

    @Test
    public void testExtractTitlesFailure() {
        String text = "";

        assertThrows(CustomAppException.class, () -> regexService.extractTitles(text));
    }

    @Test
    public void testExtractCityAndPostalCodeSuccess() throws CustomAppException {
        String text = "56 - CityName";
        String expectedCityAndPostalCode = "56 - CityName";
        String extractedCityAndPostalCode = regexService.extractCityAndPostalCode(text);

        assertEquals(expectedCityAndPostalCode, extractedCityAndPostalCode);
    }

    @Test
    public void testExtractCityAndPostalCodeFailure() {
        String text = "";

        assertThrows(CustomAppException.class, () -> regexService.extractCityAndPostalCode(text));
    }

    @Test
    public void testExtractDateSuccess() throws CustomAppException {
        String text = "vendredi 21 mai 2024";
        String expectedDate = "vendredi 21 mai 2024";
        String extractedDate = regexService.extractDate(text);

        assertEquals(expectedDate, extractedDate);
    }

    @Test
    public void testExtractDateFailure() {
        String text = "";

        assertThrows(CustomAppException.class, () -> regexService.extractDate(text));
    }
}

