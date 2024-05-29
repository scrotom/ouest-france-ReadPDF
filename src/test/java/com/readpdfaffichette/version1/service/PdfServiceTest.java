package com.readpdfaffichette.version1.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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
    private PdfService pdfService2;

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
    /*
    @Test
    @DisplayName("Test processPdfs method")
    public void testProcessPdfs() throws IOException, CustomAppException {
        Path tempDir = Files.createTempDirectory("testPdfs");
        Path pdfFile1 = tempDir.resolve("file1.pdf");
        Path pdfFile2 = tempDir.resolve("file2.pdf");

        createSamplePdf(pdfFile1, "titre. retour en\n arriere\n55 - rennes\nmardi 24 mai 2023");
        createSamplePdf(pdfFile2, "titre. retour en\n avant\n55 - rennes\nmardi 24 mai 2024");

        try (Stream<Path> paths = Files.walk(tempDir)) {
            // Mock the extractTextFromPDF and sortText methods
            when(pdfService2.extractTextFromPDF(pdfFile1.toFile(), regexService)).thenReturn("Extracted text 1");
            when(pdfService2.extractTextFromPDF(pdfFile2.toFile(), regexService)).thenReturn("Extracted text 2");
            when(pdfService2.sortText("Extracted text 1", regexService)).thenReturn("Sorted text 1");
            when(pdfService2.sortText("Extracted text 2", regexService)).thenReturn("Sorted text 2");

            StringBuilder result = pdfService.processPdfs(paths, regexService);

            String expected = "Sorted text 1\n\nSorted text 2\n\n";
            assertEquals(expected, result.toString());
        }

        // Clean up
        Files.walk(tempDir).sorted((a, b) -> b.compareTo(a)).forEach(p -> {
            try {
                Files.delete(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void createSamplePdf(Path filePath, String content) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 700);

                String[] lines = content.split("\n");
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -15);  // Adjust the vertical position for each new line
                }

                contentStream.endText();
            }

            document.save(filePath.toFile());
        }
    }*/
}