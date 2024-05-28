package com.readpdfaffichette.version1.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.readpdfaffichette.version1.exceptions.CustomAppException;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockitoAnnotations;

@ExtendWith(MockitoExtension.class)
public class FilesServiceTest {

    private Path file1;
    private Path file2;
    private Path combinedFile;
    private Path tempDirectory;
    private Path sourceFile;
    private Path destinationFile;

    @InjectMocks
    private FilesService filesService;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        // Create temporary files for testing
        file1 = Files.createTempFile("file1", ".txt");
        file2 = Files.createTempFile("file2", ".txt");
        combinedFile = Files.createTempFile("combined", ".txt");

        // Write content to the temporary files
        Files.write(file1, "Test1.\n".getBytes(StandardCharsets.UTF_8));
        Files.write(file2, "Test2.\n".getBytes(StandardCharsets.UTF_8));

        // Create a temporary directory and files for testing other methods
        tempDirectory = Files.createTempDirectory("testDir");
        sourceFile = tempDirectory.resolve("source.txt");
        destinationFile = tempDirectory.resolve("destination.txt");

        // Write some initial content to the source file
        Files.write(sourceFile, "Initial content".getBytes(StandardCharsets.UTF_8));
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Delete the temporary files
        Files.deleteIfExists(file1);
        Files.deleteIfExists(file2);
        Files.deleteIfExists(combinedFile);

        // Clean up temporary directory after tests
        Files.walk(tempDirectory)
                .map(Path::toFile)
                .forEach(File::delete);
        tempDirectory.toFile().delete();
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
    @Test
    @DisplayName("Test deleteFile method")
    public void testDeleteFile() throws IOException {
        // Ensure the source file exists
        assertEquals(true, Files.exists(sourceFile));

        // Delete the file
        filesService.deleteFile(sourceFile);

        // Ensure the file no longer exists
        assertEquals(false, Files.exists(sourceFile));
    }

    @Test
    @DisplayName("Test copyFile method")
    public void testCopyFile() throws IOException {
        // Ensure the destination file does not exist initially
        assertEquals(false, Files.exists(destinationFile));

        // Copy the file
        filesService.copyFile(sourceFile, destinationFile);

        // Ensure the destination file exists and has the correct content
        assertEquals(true, Files.exists(destinationFile));
        assertEquals("Initial content", Files.readString(destinationFile, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Test writeFile method")
    public void testWriteFile() throws IOException {
        // Write additional content to the source file
        filesService.writeFile(sourceFile, " Additional content");

        // Ensure the file has the combined content
        assertEquals("Initial content Additional content", Files.readString(sourceFile, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Test deleteFile method with non-existent file")
    public void testDeleteFileNonExistent() {
        Path nonExistentFile = tempDirectory.resolve("nonExistent.txt");

        // Verify that an IOException is thrown when trying to delete a non-existent file
        assertThrows(IOException.class, () -> filesService.deleteFile(nonExistentFile));
    }

    @Test
    @DisplayName("Test copyFile method with non-existent source file")
    public void testCopyFileNonExistentSource() {
        Path nonExistentFile = tempDirectory.resolve("nonExistent.txt");

        // Verify that an IOException is thrown when trying to copy from a non-existent source file
        assertThrows(IOException.class, () -> filesService.copyFile(nonExistentFile, destinationFile));
    }

    @Test
    @DisplayName("Test writeFile method with non-existent file")
    public void testWriteFileNonExistent() {
        Path nonExistentFile = tempDirectory.resolve("nonExistent.txt");

        // Verify that an IOException is thrown when trying to write to a non-existent file
        assertThrows(IOException.class, () -> filesService.writeFile(nonExistentFile, "Some content"));
    }
}
