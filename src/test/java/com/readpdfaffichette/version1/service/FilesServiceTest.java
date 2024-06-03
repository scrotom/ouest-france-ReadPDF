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

        file1 = Files.createTempFile("file1", ".txt");
        file2 = Files.createTempFile("file2", ".txt");
        combinedFile = Files.createTempFile("combined", ".txt");

        Files.write(file1, "Test1.\n".getBytes(StandardCharsets.UTF_8));
        Files.write(file2, "Test2.\n".getBytes(StandardCharsets.UTF_8));

        tempDirectory = Files.createTempDirectory("testDir");
        sourceFile = tempDirectory.resolve("source.txt");
        destinationFile = tempDirectory.resolve("destination.txt");

        Files.write(sourceFile, "Initial content".getBytes(StandardCharsets.UTF_8));
    }

    @AfterEach
    public void tearDown() throws IOException {

        Files.deleteIfExists(file1);
        Files.deleteIfExists(file2);
        Files.deleteIfExists(combinedFile);

        Files.walk(tempDirectory)
                .map(Path::toFile)
                .forEach(File::delete);
        tempDirectory.toFile().delete();
    }

    @Test
    @DisplayName("test pour vérifier si le méthode MergeTextFiles combine bien le contenu de deux fichier.")
    public void testMergeTextFilesSuccess() throws IOException, CustomAppException {

        filesService.mergeTextFiles(file1, file2, combinedFile);

        String combinedContent = Files.readString(combinedFile, StandardCharsets.UTF_8);

        String expectedContent = "Test1.\nTest2.\n";
        assertEquals(expectedContent, combinedContent);
    }


    @Test
    @DisplayName("test pour vérifier que la méthode MergeText renvoie bien une erreur, si l'un des deux fichier n'existe pas de base")
    public void testMergeTextFilesIOException() {

        Path nonExistentFile = Path.of("test.txt");

        CustomAppException exception = assertThrows(CustomAppException.class, () ->
                filesService.mergeTextFiles(nonExistentFile, file2, combinedFile)
        );

        assertEquals("erreur lors de l'assemblage des deux fichiers : " + "test.txt", exception.getMessage());
    }
    @Test
    @DisplayName("test pour vérifier que la méthode DeleteFile supprime bien le fichier ")
    public void testDeleteFile() throws IOException {

        assertEquals(true, Files.exists(sourceFile));

        filesService.deleteFile(sourceFile);

        assertEquals(false, Files.exists(sourceFile));
    }

    @Test
    @DisplayName("test pour vérifier que la méthode DeleteFile renvoie bien une erreur si le fichier n'existe pas de base ")
    public void testDeleteFileNonExistent() {
        Path nonExistentFile = tempDirectory.resolve("nonExistent.txt");

        assertThrows(IOException.class, () -> filesService.deleteFile(nonExistentFile));
    }

    @Test
    @DisplayName("test pour vérifier que la méthode CopyFile copie bien le fichier")
    public void testCopyFile() throws IOException {

        assertEquals(false, Files.exists(destinationFile));

        filesService.copyFile(sourceFile, destinationFile);

        assertEquals(true, Files.exists(destinationFile));
        assertEquals("Initial content", Files.readString(destinationFile, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("test pour vérifier que la méthode CopyFile renvoie bien une erreur si le fichier n'existe pas de base")
    public void testCopyFileNonExistentSource() {
        Path nonExistentFile = tempDirectory.resolve("nonExistent.txt");

        assertThrows(IOException.class, () -> filesService.copyFile(nonExistentFile, destinationFile));
    }

    @Test
    @DisplayName("test pour vérifier si la méthode WriteFile écrit bien dans le fichier")
    public void testWriteFile() throws IOException {
        filesService.writeFile(sourceFile, " Additional content");

        assertEquals("Initial content Additional content", Files.readString(sourceFile, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("test pour vérifier si la méthode WriteFile renvoei bien une erreur si le fichier n'existe pas de base")
    public void testWriteFileNonExistent() {
        Path nonExistentFile = tempDirectory.resolve("nonExistent.txt");

        assertThrows(IOException.class, () -> filesService.writeFile(nonExistentFile, "Some content"));
    }
}
