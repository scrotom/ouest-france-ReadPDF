/*
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.readpdfaffichette.version1.service.FilesService;
import com.readpdfaffichette.version1.service.RegexService;
import com.readpdfaffichette.version1.service.TreatmentService;
import com.readpdfaffichette.version1.service.PdfService;
import com.readpdfaffichette.version1.exceptions.CustomAppException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SpringApplication.class, Files.class, FilesService.class})
public class TreatmentServiceTest {

    @Mock
    private ConfigurableApplicationContext context;

    @Mock
    private RegexService regexService;

    @Mock
    private FilesService filesService;

    @Mock
    private PdfService pdfService;

    @InjectMocks
    private TreatmentService treatmentService;

    private Path tempDirectory;

    @BeforeEach
    public void setUp() throws IOException, CustomAppException {
        MockitoAnnotations.openMocks(this);

        // Create a temporary directory for testing
        tempDirectory = Files.createTempDirectory("testDir");

        // Mock the FilesService methods
        when(filesService.getLink()).thenReturn(tempDirectory.toString());
        when(filesService.getStyleCssSaveplace()).thenReturn(tempDirectory.resolve("styles.css"));
        when(filesService.getStyleCssSource()).thenReturn(tempDirectory.resolve("sourceStyles.css"));
        when(filesService.getTextFile1()).thenReturn(tempDirectory.resolve("partie1.txt"));
        when(filesService.getTextFile1Saveplace()).thenReturn(tempDirectory.resolve("savedPartie1.txt"));
        when(filesService.getTextFile3()).thenReturn(tempDirectory.resolve("partie3.txt"));
        when(filesService.getMergedFile()).thenReturn(tempDirectory.resolve("merged.html"));

        // Mock the PdfService methods
        when(pdfService.extractTextFromPDF(any(File.class), eq(regexService))).thenReturn("extracted text");
        when(pdfService.sortText(anyString(), eq(regexService))).thenReturn("sorted text");

        // Mock the SpringApplication.run method
        PowerMockito.mockStatic(SpringApplication.class);
        when(SpringApplication.run(any(Class.class), any(String[].class))).thenReturn(context);

        // Create dummy files for testing
        Files.createFile(tempDirectory.resolve("test.pdf"));
        Files.write(tempDirectory.resolve("sourceStyles.css"), "source css content".getBytes(StandardCharsets.UTF_8));
        Files.write(tempDirectory.resolve("partie1.txt"), "partie1 content".getBytes(StandardCharsets.UTF_8));
        Files.write(tempDirectory.resolve("partie3.txt"), "partie3 content".getBytes(StandardCharsets.UTF_8));
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Clean up temporary directory after tests
        Files.walk(tempDirectory)
                .map(Path::toFile)
                .forEach(File::delete);
        tempDirectory.toFile().delete();
    }

    @Test
    @DisplayName("Test readpdf method success")
    public void testReadpdfSuccess() throws IOException, CustomAppException {
        // Run the treatment
        treatmentService.readpdf(new String[]{});

        // Verify file operations
        assertEquals("source css content", Files.readString(tempDirectory.resolve("styles.css"), StandardCharsets.UTF_8));
        assertEquals("partie1 content\n\nsorted text\n\n", Files.readString(tempDirectory.resolve("savedPartie1.txt"), StandardCharsets.UTF_8));

        // Verify merging files
        String mergedContent = Files.readString(tempDirectory.resolve("merged.html"), StandardCharsets.UTF_8);
        assertEquals("partie1 content\n\nsorted text\n\npartie3 content", mergedContent);

        // Verify the interactions with mocked methods
        verify(filesService, times(1)).getLink();
        verify(filesService, times(1)).getStyleCssSource();
        verify(filesService, times(1)).getStyleCssSaveplace();
        verify(filesService, times(1)).getTextFile1();
        verify(filesService, times(1)).getTextFile1Saveplace();
        verify(filesService, times(1)).getTextFile3();
        verify(filesService, times(1)).getMergedFile();

        verify(pdfService, times(1)).extractTextFromPDF(any(File.class), eq(regexService));
        verify(pdfService, times(1)).sortText(anyString(), eq(regexService));
    }

    @Test
    @DisplayName("Test readpdf method with non-existent directory")
    public void testReadpdfDirectoryNotFound() throws IOException, CustomAppException {
        // Mock getLink to return a non-existent directory
        when(filesService.getLink()).thenReturn("nonExistentDir");

        // Capture the output stream to check for error message
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the treatment
        treatmentService.readpdf(new String[]{});

        // Verify the error message
        assertEquals("erreur : Le chemin n'ammène pas à un dossier.\n", outContent.toString());

        // Restore the original output stream
        System.setOut(originalOut);
    }
}
 */
