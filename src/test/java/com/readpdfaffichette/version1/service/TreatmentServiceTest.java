package com.readpdfaffichette.version1.service;

import com.readpdfaffichette.version1.exceptions.CustomAppException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TreatmentServiceTest {

    @Mock
    private PdfService pdfService;

    @Mock
    private FilesService filesService;

    @Mock
    private RegexService regexService;

    @InjectMocks
    private TreatmentService treatmentService;

    private Path tempDir;
    private Path textFile1;
    private Path textFile3;
    private Path mergedFile;

    @BeforeEach
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("testDir");
        textFile1 = tempDir.resolve("partie1.txt");
        textFile3 = tempDir.resolve("partie3.txt");
        mergedFile = tempDir.resolve("merged.html");

        // Mock the FilesService methods
        when(filesService.getLink()).thenReturn(tempDir.toString());
        when(filesService.getTextFile1()).thenReturn(textFile1);
        when(filesService.getTextFile1Saveplace()).thenReturn(tempDir.resolve("savedPartie1.txt"));
        when(filesService.getTextFile3()).thenReturn(textFile3);
        when(filesService.getMergedFile()).thenReturn(mergedFile);

        Files.write(textFile1, "Partie 1 content".getBytes());
        Files.write(textFile3, "Partie 3 content".getBytes());
    }

    @Test
    public void testReadPdfSuccess() throws IOException, CustomAppException {
        Path pdfFile = tempDir.resolve("test.pdf");
        createSamplePdf(pdfFile, "PDF content");

        try (Stream<Path> paths = Files.walk(tempDir)) {
            when(pdfService.processPdfs(paths, regexService)).thenReturn(new StringBuilder("Extracted text\n"));

            treatmentService.readpdf(new String[]{});

            verify(filesService).copyFile(textFile1, tempDir.resolve("savedPartie1.txt"));
            verify(filesService).writeFile(tempDir.resolve("savedPartie1.txt"), "Extracted text\n");
            verify(filesService).mergeTextFiles(tempDir.resolve("savedPartie1.txt"), textFile3, mergedFile);
            verify(filesService).deleteFile(tempDir.resolve("savedPartie1.txt"));
        }
    }

    @Test
    public void testReadPdfFailure() throws IOException, CustomAppException {
        Path invalidDir = tempDir.resolve("invalidDir");
        when(filesService.getLink()).thenReturn(invalidDir.toString());

        treatmentService.readpdf(new String[]{});

        verify(filesService, never()).copyFile(any(Path.class), any(Path.class));
        verify(filesService, never()).writeFile(any(Path.class), anyString());
        verify(filesService, never()).mergeTextFiles(any(Path.class), any(Path.class), any(Path.class));
        verify(filesService, never()).deleteFile(any(Path.class));
    }

    private void createSamplePdf(Path filePath, String content) throws IOException {
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

            document.save(filePath.toFile());
        }
    }
}
