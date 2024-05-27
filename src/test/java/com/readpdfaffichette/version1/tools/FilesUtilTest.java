package com.readpdfaffichette.version1.tools;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.readpdfaffichette.version1.exceptions.CustomAppException;

public class FilesUtilTest {

    @Test
    public void testMergeTextFilesSuccess() {
        Path file1Path = Paths.get("C:/Users/tom.lefevrebonzon/Desktop/projetAffichettes-readpdf/version1/src/test/java/com/readpdfaffichette/version1/resources/file1.txt");
        Path file2Path = Paths.get("C:/Users/tom.lefevrebonzon/Desktop/projetAffichettes-readpdf/version1/src/test/java/com/readpdfaffichette/version1/resources/file2.txt");
        Path combinedFilePath = Paths.get("C:/Users/tom.lefevrebonzon/Desktop/projetAffichettes-readpdf/version1/src/test/java/com/readpdfaffichette/version1/resources/file1.txt/combined.txt");

        assertDoesNotThrow(() -> FilesUtil.mergeTextFiles(file1Path, file2Path, combinedFilePath));
    }

    @Test
    public void testMergeTextFilesIOException() {
        Path file1Path = Paths.get("src/test/resources/nonexistent1.txt");
        Path file2Path = Paths.get("src/test/resources/nonexistent2.txt");
        Path combinedFilePath = Paths.get("src/test/resources/combined.txt");

        assertThrows(CustomAppException.class, () -> FilesUtil.mergeTextFiles(file1Path, file2Path, combinedFilePath));
    }
}
