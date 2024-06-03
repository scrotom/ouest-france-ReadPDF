package com.readpdfaffichette.version1.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.readpdfaffichette.version1.exceptions.CustomAppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

public class TreatmentServiceTest {

    @InjectMocks
    private TreatmentService treatmentService;

    @Mock
    private PdfService pdfService;

    @Mock
    private FilesService filesService;

    @Mock
    private RegexService regexService;

    @Mock
    private FtpService ftpService;

    private Path dynamicMergedFilePath;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Création d'un chemin dynamique pour le fichier de sortie
        dynamicMergedFilePath = Paths.get(System.getProperty("user.home"), "output", "affichettes_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".html");

        //Injection des propriétés de test du FTP
        ReflectionTestUtils.setField(treatmentService, "ftpServer", "ftp.example.com");
        ReflectionTestUtils.setField(treatmentService, "ftpPort", 21);
        ReflectionTestUtils.setField(treatmentService, "ftpUser", "ftpuser");
        ReflectionTestUtils.setField(treatmentService, "ftpPassword", "ftppassword");
        ReflectionTestUtils.setField(treatmentService, "ftpUploadPath", "/remote/path/affichettes.html");
    }

    @Test
    @DisplayName("test pour vérifier que la méthode ReadPdf s'exécute bien dans un cas normal")
    public void testReadpdfSuccess() throws IOException, CustomAppException {
        String[] args = {};
        File folder = new File("src/test/resources/");
        Path textFile1 = Paths.get("src/test/resources/partie1.txt");
        Path textFile1Saveplace = Paths.get("src/test/resources/partie1_saved.txt");
        Path textFile3 = Paths.get("src/test/resources/partie3.txt");

        when(filesService.getLink()).thenReturn(folder.getPath());
        when(filesService.getTextFile1()).thenReturn(textFile1);
        when(filesService.getTextFile1Saveplace()).thenReturn(textFile1Saveplace);
        when(filesService.getTextFile3()).thenReturn(textFile3);
        when(filesService.getMergedFilePath()).thenReturn(dynamicMergedFilePath);

        doNothing().when(filesService).copyFile(textFile1, textFile1Saveplace);
        doNothing().when(filesService).writeFile(any(Path.class), anyString());
        doNothing().when(filesService).mergeTextFiles(textFile1Saveplace, textFile3, dynamicMergedFilePath);
        doNothing().when(filesService).deleteFile(textFile1Saveplace);
        doNothing().when(ftpService).uploadFileToFTP(anyString(), anyInt(), anyString(), anyString(), anyString(), anyString());

        StringBuilder processedText = new StringBuilder("Processed text");
        when(pdfService.processPdfs(any(Stream.class), eq(regexService))).thenReturn(processedText);

        treatmentService.readpdf(args);

        verify(filesService, times(1)).copyFile(textFile1, textFile1Saveplace);
        verify(pdfService, times(1)).processPdfs(any(Stream.class), eq(regexService));
        verify(filesService, times(1)).writeFile(textFile1Saveplace, processedText.toString());
        verify(filesService, times(1)).mergeTextFiles(textFile1Saveplace, textFile3, dynamicMergedFilePath);
        verify(filesService, times(1)).deleteFile(textFile1Saveplace);
        verify(ftpService, times(1)).uploadFileToFTP(anyString(), anyInt(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("test pour vérifier que la méthode ReadPdf n'exécute pas les étapes suivantes en cas de non présence d'un dossier")
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
    @DisplayName("test pour vérifier que la méthode ReadPdf gère bien les erreurs de traitement des pdf")
    public void testReadpdfExceptionDuringProcessing() throws IOException, CustomAppException {
        String[] args = {};
        File folder = new File("src/test/resources/");
        Path textFile1 = Paths.get("src/test/resources/partie1.txt");
        Path textFile1Saveplace = Paths.get("src/test/resources/partie1_saved.txt");
        Path textFile3 = Paths.get("src/test/resources/partie3.txt");

        when(filesService.getLink()).thenReturn(folder.getPath());
        when(filesService.getTextFile1()).thenReturn(textFile1);
        when(filesService.getTextFile1Saveplace()).thenReturn(textFile1Saveplace);
        when(filesService.getTextFile3()).thenReturn(textFile3);
        when(filesService.getMergedFilePath()).thenReturn(dynamicMergedFilePath);

        doNothing().when(filesService).copyFile(textFile1, textFile1Saveplace);

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
