package com.readpdfaffichette.version1.service;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.readpdfaffichette.version1.exceptions.CustomAppException;

public class PdfServiceTest {

    @InjectMocks
    private PdfService pdfService;

    @Mock
    private RegexService regexService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExtractTextFromPDFSuccess() throws IOException, CustomAppException {
        File file = new File("C:/Users/tom.lefevrebonzon/Desktop/projetAffichettes-readpdf/gitkraken/projet-1-ouest-france-readpdf/src/test/java/com/readpdfaffichette/version1/resources/test.pdf");
        String expectedText = "ceci est un test d'extraction de texte sur ce pdf. \r\n";
        String extractedText = pdfService.extractTextFromPDF(file, regexService);

        assertEquals(expectedText, extractedText);
    }

    @Test
    public void testExtractTextFromPDFIOException() {
        File file = new File("src/test/resources/nonexistent.pdf");

        assertThrows(CustomAppException.class, () -> pdfService.extractTextFromPDF(file, regexService));
    }

    @Test
    public void testSortTextSuccess() throws CustomAppException {
        String inputText = "Sample text for sorting.";
        
        // Mock the responses for the regex methods
        when(regexService.extractTitles(inputText)).thenReturn(new String[] {"Title1.", "Subtitle1", "Title2.", "Subtitle2"});
        when(regexService.extractCityAndPostalCode(inputText)).thenReturn("City - 12345");
        when(regexService.extractDate(inputText)).thenReturn("January 1, 2024");

        String expectedSortedText = "<TR><TD class=\"tableauAffichette\" width=\"25%\">City - 12345</TD><TD class=\"tableauAffichette\" width=\"25%\">January 1, 2024</TD><TD class=\"tableauAffichette\"><u>Title1.</u> Subtitle1<BR><u>Title2.</u> Subtitle2</TD></TR>";
        String sortedText = pdfService.sortText(inputText, regexService);

        assertEquals(expectedSortedText, sortedText);
    }

    @Test
    public void testSortTextFailure() throws CustomAppException {
        String inputText = "Sample text for sorting.";

        // Mock the responses for the regex methods to throw an exception
        when(regexService.extractTitles(inputText)).thenThrow(new CustomAppException("Error extracting titles"));

        assertThrows(CustomAppException.class, () -> pdfService.sortText(inputText, regexService));
    }
}