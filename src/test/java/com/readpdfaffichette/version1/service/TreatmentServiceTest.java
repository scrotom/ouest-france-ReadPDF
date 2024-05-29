package com.readpdfaffichette.version1.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.readpdfaffichette.version1.exceptions.CustomAppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TreatmentServiceTest {

    @InjectMocks
    private TreatmentService treatmentService;

    @Mock
    private PdfService pdfService;

    @Mock
    private FilesService filesService;

    @Mock
    private RegexService regexService;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testReadpdfSuccess() throws IOException, CustomAppException {
        String[] args = {};
        File folder = new File("src/test/resources/");
        Path textFile1 = Paths.get("src/test/resources/partie1.txt");
        Path textFile1Saveplace = Paths.get("src/test/resources/partie1_saved.txt");
        Path textFile3 = Paths.get("src/test/resources/partie3.txt");
        Path mergedFile = Paths.get("src/test/resources/merged.html");

        when(filesService.getLink()).thenReturn(folder.getPath());
        when(filesService.getTextFile1()).thenReturn(textFile1);
        when(filesService.getTextFile1Saveplace()).thenReturn(textFile1Saveplace);
        when(filesService.getTextFile3()).thenReturn(textFile3);
        when(filesService.getMergedFile()).thenReturn(mergedFile);

        doNothing().when(filesService).copyFile(textFile1, textFile1Saveplace);
        doNothing().when(filesService).writeFile(any(Path.class), anyString());
        doNothing().when(filesService).mergeTextFiles(textFile1Saveplace, textFile3, mergedFile);
        doNothing().when(filesService).deleteFile(textFile1Saveplace);

        StringBuilder processedText = new StringBuilder("Processed text");
        when(pdfService.processPdfs(any(Stream.class), eq(regexService))).thenReturn(processedText);

        treatmentService.readpdf(args);

        verify(filesService, times(1)).copyFile(textFile1, textFile1Saveplace);
        verify(pdfService, times(1)).processPdfs(any(Stream.class), eq(regexService));
        verify(filesService, times(1)).writeFile(textFile1Saveplace, processedText.toString());
        verify(filesService, times(1)).mergeTextFiles(textFile1Saveplace, textFile3, mergedFile);
        verify(filesService, times(1)).deleteFile(textFile1Saveplace);
    }

    @Test
    public void testReadpdfFolderNotDirectory() throws IOException, CustomAppException {
        String[] args = {};
        File nonFolder = new File("src/test/resources/nonFolder.txt");

        when(filesService.getLink()).thenReturn(nonFolder.getPath());

        treatmentService.readpdf(args);

        verify(filesService, never()).copyFile(any(Path.class), any(Path.class));
        verify(pdfService, never()).processPdfs(any(Stream.class), eq(regexService));
        verify(filesService, never()).writeFile(any(Path.class), anyString());
        verify(filesService, never()).mergeTextFiles(any(Path.class), any(Path.class), any(Path.class));
        verify(filesService, never()).deleteFile(any(Path.class));
    }

    @Test
    public void testReadpdfExceptionDuringProcessing() throws IOException, CustomAppException {
        String[] args = {};
        File folder = new File("src/test/resources/");
        Path textFile1 = Paths.get("src/test/resources/partie1.txt");
        Path textFile1Saveplace = Paths.get("src/test/resources/partie1_saved.txt");
        Path textFile3 = Paths.get("src/test/resources/partie3.txt");
        Path mergedFile = Paths.get("src/test/resources/merged.html");

        when(filesService.getLink()).thenReturn(folder.getPath());
        when(filesService.getTextFile1()).thenReturn(textFile1);
        when(filesService.getTextFile1Saveplace()).thenReturn(textFile1Saveplace);
        when(filesService.getTextFile3()).thenReturn(textFile3);
        when(filesService.getMergedFile()).thenReturn(mergedFile);

        doNothing().when(filesService).copyFile(textFile1, textFile1Saveplace);

        // Ensure folder exists and is recognized as a directory
        if (!folder.exists()) {
            folder.mkdirs();
        }

        when(pdfService.processPdfs(any(Stream.class), eq(regexService))).thenThrow(new CustomAppException("Processing error"));

        assertThrows(CustomAppException.class, () -> {
            treatmentService.readpdf(args);
        });

        verify(filesService, times(1)).copyFile(textFile1, textFile1Saveplace);
        verify(pdfService, times(1)).processPdfs(any(Stream.class), eq(regexService));
        verify(filesService, never()).writeFile(any(Path.class), anyString());
        verify(filesService, never()).mergeTextFiles(any(Path.class), any(Path.class), any(Path.class));
        verify(filesService, never()).deleteFile(any(Path.class));
    }
}
