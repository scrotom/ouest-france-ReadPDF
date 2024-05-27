package com.readpdfaffichette.version1.tools;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import com.readpdfaffichette.version1.exceptions.CustomAppException;

public class RegexUtilTest {

    @InjectMocks
    private RegexUtil regexUtil;

    @Value("${regex.titles}")
    private String regexTitles = "(.*?)(?=(?:PHOTO :|\\d{2} -))";

    @Value("${regex.city}")
    private String regexCity = "\\d{2} - .+";

    @Value("${regex.date}")
    private String regexDate = "([a-zA-Z]+ \\d{1,2} \\w+ \\d{4})";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        regexUtil.setRegexTitles(regexTitles);
        regexUtil.setRegexCity(regexCity);
        regexUtil.setRegexDate(regexDate);
    }

    @Test
    public void testExtractTitlesSuccess() throws CustomAppException {
        String text = "Title1.\nSubtitle1\nTitle2.\nSubtitle2";
        String[] expectedTitles = { "Title1.", "Subtitle1", "Title2.", "Subtitle2" };
        String[] extractedTitles = regexUtil.extractTitles(text);

        assertArrayEquals(expectedTitles, extractedTitles);
    }

    @Test
    public void testExtractTitlesFailure() {
        String text = "";

        assertThrows(CustomAppException.class, () -> regexUtil.extractTitles(text));
    }

    @Test
    public void testExtractCityAndPostalCodeSuccess() throws CustomAppException {
        String text = "56 - CityName";
        String expectedCityAndPostalCode = "56 - CityName";
        String extractedCityAndPostalCode = regexUtil.extractCityAndPostalCode(text);

        assertEquals(expectedCityAndPostalCode, extractedCityAndPostalCode);
    }

    @Test
    public void testExtractCityAndPostalCodeFailure() {
        String text = "";

        assertThrows(CustomAppException.class, () -> regexUtil.extractCityAndPostalCode(text));
    }

    @Test
    public void testExtractDateSuccess() throws CustomAppException {
        String text = "vendredi 21 mai 2024";
        String expectedDate = "vendredi 21 mai 2024";
        String extractedDate = regexUtil.extractDate(text);

        assertEquals(expectedDate, extractedDate);
    }

    @Test
    public void testExtractDateFailure() {
        String text = "";

        assertThrows(CustomAppException.class, () -> regexUtil.extractDate(text));
    }
}

