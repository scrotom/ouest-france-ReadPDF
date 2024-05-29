package com.readpdfaffichette.version1.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    /*@Test
    @DisplayName("Test processPdfs method")
    public void testProcessPdfs() throws IOException, CustomAppException {
        Path tempDir = Files.createTempDirectory("testPdfs");
        Path pdfFile1 = tempDir.resolve("file1.pdf");
        Path pdfFile2 = tempDir.resolve("file2.pdf");

        Files.write(pdfFile1, "PDF content 1".getBytes());
        Files.write(pdfFile2, "PDF content 2".getBytes());

        try (Stream<Path> paths = Files.walk(tempDir)) {
            // Mock the extractTextFromPDF and sortText methods
            when(pdfService.extractTextFromPDF(pdfFile1.toFile(), regexService)).thenReturn("Extracted text 1");
            when(pdfService.extractTextFromPDF(pdfFile2.toFile(), regexService)).thenReturn("Extracted text 2");
            when(pdfService.sortText("Extracted text 1", regexService)).thenReturn("Sorted text 1");
            when(pdfService.sortText("Extracted text 2", regexService)).thenReturn("Sorted text 2");

            StringBuilder result = pdfService.processPdfs(paths, regexService);

            String expected = "Sorted text 1\n\nSorted text 2\n\n";
            assertEquals(expected, result.toString());
        }

        // Clean des fichiers de tests
        Files.walk(tempDir).sorted((a, b) -> b.compareTo(a)).forEach(p -> {
            try {
                Files.delete(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }*/
}