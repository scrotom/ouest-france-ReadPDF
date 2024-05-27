package com.readpdfaffichette.version1.tools;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.readpdfaffichette.version1.exceptions.CustomAppException;

public class PdfUtilTest {

    @InjectMocks
    private PdfUtil pdfUtil;

    @Mock
    private RegexUtil regexUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExtractTextFromPDFSuccess() throws IOException, CustomAppException {
        File file = new File("C:/Users/tom.lefevrebonzon/Desktop/projetAffichettes-readpdf/version1/src/test/java/com/readpdfaffichette/version1/resources/test.pdf");
        String expectedText = "ceci est un test d'extraction de texte sur ce pdf. \n";
        String extractedText = PdfUtil.extractTextFromPDF(file, regexUtil);

        assertEquals(expectedText, extractedText);
    }

    @Test
    public void testExtractTextFromPDFIOException() {
        File file = new File("src/test/resources/nonexistent.pdf");

        assertThrows(CustomAppException.class, () -> PdfUtil.extractTextFromPDF(file, regexUtil));
    }
}
