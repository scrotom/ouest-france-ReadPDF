package com.readpdfaffichette.version1.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.readpdfaffichette.version1.service.FilesService;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.readpdfaffichette.version1.exceptions.CustomAppException;
import org.mockito.InjectMocks;

public class FilesServiceTest {

    private Path file1;
    private Path file2;
    private Path combinedFile;

    @InjectMocks
    private FilesService filesService;

    @BeforeEach
    public void setUp() throws IOException {
        // Create temporary files for testing
        file1 = Files.createTempFile("file1", ".txt");
        file2 = Files.createTempFile("file2", ".txt");
        combinedFile = Files.createTempFile("combined", ".txt");

        // Write content to the temporary files
        Files.write(file1, "Test1.\n".getBytes(StandardCharsets.UTF_8));
        Files.write(file2, "Test2.\n".getBytes(StandardCharsets.UTF_8));
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Delete the temporary files
        Files.deleteIfExists(file1);
        Files.deleteIfExists(file2);
        Files.deleteIfExists(combinedFile);
    }

    @Test
    public void testMergeTextFilesSuccess() throws IOException, CustomAppException {
        // Merge the files
        filesService.mergeTextFiles(file1, file2, combinedFile);

        // Read the combined file
        String combinedContent = Files.readString(combinedFile, StandardCharsets.UTF_8);

        // Verify the combined content
        String expectedContent = "Test1.\nTest2.\n";
        assertEquals(expectedContent, combinedContent);
    }

    @Test
    public void testMergeTextFilesIOException() {
        // Create a path to a non-existent file
        Path nonExistentFile = Path.of("test.txt");

        // Verify that the CustomAppException is thrown when merging with a non-existent file
        CustomAppException exception = assertThrows(CustomAppException.class, () ->
            filesService.mergeTextFiles(nonExistentFile, file2, combinedFile)
        );

        assertEquals("erreur lors de l'assemblage des deux fichiers : " + "test.txt", exception.getMessage());

    }
}
