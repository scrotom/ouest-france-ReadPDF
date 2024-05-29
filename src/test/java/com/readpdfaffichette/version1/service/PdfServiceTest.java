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
    private RegexService regexService;

    @Mock
    private PdfService pdfServiceMock;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

    }

    //méthode pour créer un fichier pdf temporaire, dans le but d'effectuer les tests des méthodes dessus
    private Path createTempPdfFile(String content) throws IOException {
        Path tempFile = Files.createTempFile("test", ".pdf");
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText(content);
                contentStream.endText();
            }
            document.save(tempFile.toFile());
        }
        return tempFile;
    }

    @Test
    @DisplayName("test pour vérifier si la méthode ExtractTextFromPDF extrait bien le texte du pdf")
    public void testExtractTextFromPDFSuccess() throws IOException, CustomAppException {
        String pdfContent = "ceci est un test d'extraction de texte sur ce pdf.";
        Path tempPdfPath = createTempPdfFile(pdfContent);
        File file = tempPdfPath.toFile();

        String expectedText = "ceci est un test d'extraction de texte sur ce pdf.\r\n";
        String extractedText = pdfService.extractTextFromPDF(file, regexService);

        assertEquals(expectedText, extractedText);

        // Clean up the temporary file
        Files.deleteIfExists(tempPdfPath);
    }


    @Test
    @DisplayName("test pour vérifier si la méthode ExtractTextFromPDF renvoie bien une erreur IOException si le fichier n'existe pas de base")
    public void testExtractTextFromPDFIOException() {
        File file = new File("src/test/resources/nonexistent.pdf");

        assertThrows(CustomAppException.class, () -> pdfService.extractTextFromPDF(file, regexService));
    }

    @Test
    @DisplayName("test pour vérifier si la méthode SortTextSuccess trie bien le texte selon les critères demandés")
    public void testSortTextSuccess() throws CustomAppException {
        String inputText = "Sample text for sorting.";

        // Mock les réponses des méthodes regex
        when(regexService.extractTitles(inputText)).thenReturn(new String[]{"Title1.", "Subtitle1", "Title2.", "Subtitle2"});
        when(regexService.extractCityAndPostalCode(inputText)).thenReturn("City - 12345");
        when(regexService.extractDate(inputText)).thenReturn("January 1, 2024");

        String expectedSortedText = "<TR><TD class=\"tableauAffichette\" width=\"25%\">City - 12345</TD><TD class=\"tableauAffichette\" width=\"25%\">January 1, 2024</TD><TD class=\"tableauAffichette\"><u>Title1.</u> Subtitle1<BR><u>Title2.</u> Subtitle2</TD></TR>";
        String sortedText = pdfService.sortText(inputText, regexService);

        assertEquals(expectedSortedText, sortedText);
    }

    @Test
    public void testSortTextFailure() throws CustomAppException {
        String inputText = "Sample text for sorting.";

        // Mock les réponses des méthodes regex pour renvoyer une erreur
        when(regexService.extractTitles(inputText)).thenThrow(new CustomAppException("Error extracting titles"));

        assertThrows(CustomAppException.class, () -> pdfService.sortText(inputText, regexService));
    }

    @Test
    public void testProcessPdfsSuccess() throws IOException, CustomAppException {
        String pdfContent1 = "The Voice. Un casting lors d’un spectacle à Auray Belle-Île. L’épicier solidaire détourne 348 000 € 56 - Auray mercredi 29 mai 2024";
        String pdfContent2 = "Groix. Un jardin des insectes pour la biodiversité 56 - Groix jeudi 30 mai 2024";

        Path tempPdfPath1 = createTempPdfFile(pdfContent1);
        Path tempPdfPath2 = createTempPdfFile(pdfContent2);

        String extractedText1 = "The Voice. Un casting lors d’un spectacle à Auray Belle-Île. L’épicier solidaire détourne 348 000 € 56 - Auray mercredi 29 mai 2024";
        String sortedText1 = "<TR><TD class=\"tableauAffichette\" width=\"25%\">56 - Auray</TD><TD class=\"tableauAffichette\" width=\"25%\">mercredi 29 mai 2024</TD><TD class=\"tableauAffichette\"><u>The Voice.</u> Un casting lors d’un spectacle à Auray<BR><u>Belle-Île.</u> L’épicier solidaire détourne 348 000 €</TD></TR>";

        String extractedText2 = "Groix. Un jardin des insectes pour la biodiversité 56 - Vannes jeudi 30 mai 2024";
        String sortedText2 = "<TR><TD class=\"tableauAffichette\" width=\"25%\">56 - Vannes</TD><TD class=\"tableauAffichette\" width=\"25%\">jeudi 30 mai 2024</TD><TD class=\"tableauAffichette\"><u>Groix.</u> Un casting lors d’un spectacle à Auray</TD></TR>";

        //mock les réponses des méthodes regex
        when(regexService.extractTitles(extractedText1)).thenReturn(new String[]{"The Voice.", "Un casting lors d’un spectacle à Auray", "Belle-Île.", "L’épicier solidaire détourne 348 000 €"});
        when(regexService.extractCityAndPostalCode(extractedText1)).thenReturn("56 - Auray");
        when(regexService.extractDate(extractedText1)).thenReturn("mercredi 29 mai 2024");

        when(regexService.extractTitles(extractedText2)).thenReturn(new String[]{"Groix.", "Un jardin des insectes pour la biodiversité"});
        when(regexService.extractCityAndPostalCode(extractedText2)).thenReturn("56 - Vannes");
        when(regexService.extractDate(extractedText2)).thenReturn("jeudi 30 mai 2024");

        // Mock les réponses des méthodes pdf
        when(pdfServiceMock.extractTextFromPDF(tempPdfPath1.toFile(), regexService)).thenReturn(extractedText1);
        when(pdfServiceMock.sortText(extractedText1, regexService)).thenReturn(sortedText1);

        when(pdfServiceMock.extractTextFromPDF(tempPdfPath2.toFile(), regexService)).thenReturn(extractedText2);
        when(pdfServiceMock.sortText(extractedText2, regexService)).thenReturn(sortedText2);

        try (Stream<Path> paths = Stream.of(tempPdfPath1, tempPdfPath2)) {
            // Use pdfServiceMock instead of pdfService
            when(pdfServiceMock.processPdfs(paths, regexService)).thenCallRealMethod();
            StringBuilder result = pdfServiceMock.processPdfs(paths, regexService);
            String expected = sortedText1 + "\n\n" + sortedText2 + "\n\n";
            assertEquals(expected, result.toString());
        }

        Files.deleteIfExists(tempPdfPath1);
        Files.deleteIfExists(tempPdfPath2);
    }

    @Test
    public void testProcessPdfsWithNonPdfFile() throws IOException, CustomAppException {
        Path nonPdfPath = Path.of("src/test/resources/test.txt");

        try (Stream<Path> paths = Stream.of(nonPdfPath)) {
            StringBuilder result = pdfService.processPdfs(paths, regexService);
            assertEquals("", result.toString());
        }
    }

    @Test
    public void testProcessPdfsWithException() throws IOException, CustomAppException {
        String pdfContent = "ceci est un test d'extraction de texte sur ce pdf.";
        Path tempPdfPath = createTempPdfFile(pdfContent);
        File file = tempPdfPath.toFile();


        when(pdfServiceMock.extractTextFromPDF(file, regexService)).thenThrow(new CustomAppException("Erreur lors de l'extraction du texte sur le pdf"));

        try (Stream<Path> paths = Stream.of(tempPdfPath)) {

            when(pdfServiceMock.processPdfs(paths, regexService)).thenCallRealMethod();
            StringBuilder result = pdfServiceMock.processPdfs(paths, regexService);
            assertEquals("", result.toString());
        }
        Files.deleteIfExists(tempPdfPath);
    }
}
