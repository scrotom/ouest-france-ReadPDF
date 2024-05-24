package com.readpdfaffichette.version1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.readpdfaffichette.version1.classes.filesInfo;
import com.readpdfaffichette.version1.classes.reggex;

@SpringBootTest
class Version1ApplicationTests {

    private reggex reggex;

	@BeforeEach
    void setUp() {
        reggex = new reggex();
        // Configuration des regex fictifs
        reggex.setReggexTitles("(.+)");
        reggex.setReggexCity("\\d{2} - \\w+");
        reggex.setReggexDate("\\w+ \\d{1,2} \\w+ \\d{4}");
    }

    // Test des méthodes de la classe filesInfo.java

    @Test
    void testMergeTextFiles() throws IOException {
        // Chemins des fichiers de test
        Path file1Path = Paths.get("src/test/java/com/readpdfaffichette/version1/resources/file1.txt");
        Path file2Path = Paths.get("src/test/java/com/readpdfaffichette/version1/resources/file2.txt");
        Path combinedFilePath = Paths.get("src/test/java/com/readpdfaffichette/version1/resources/merged.txt");

        // Contenus attendus des fichiers
        String file1Content = "a";
        String file2Content = "b";

        // Écriture des contenus de test dans les fichiers
        Files.write(file1Path, file1Content.getBytes(StandardCharsets.UTF_8));
        Files.write(file2Path, file2Content.getBytes(StandardCharsets.UTF_8));

        // Appel de la méthode à tester
        filesInfo.mergeTextFiles(file1Path, file2Path, combinedFilePath);

        // Vérification du contenu du fichier combiné
        String expectedCombinedContent = file1Content + file2Content;
        String actualCombinedContent = Files.readString(combinedFilePath, StandardCharsets.UTF_8);
        assertEquals(expectedCombinedContent, actualCombinedContent);

        // Nettoyage des fichiers de test
        Files.deleteIfExists(combinedFilePath);
    }

    // Test des méthodes de la classe reggex.java

    @Test
    void testExtractTitlesWithTwoTitles() {
        String text = "Belle-Ile-en-Mer.\nUn festival de\nl'humour attirant\nEn Bretagne. Un\nsecteur du livre\nfoisonnant";
        String[] expected = {"Belle-Ile-en-Mer.", "Un festival de\nl'humour attirant", "En Bretagne.", "Un\nsecteur du livre\nfoisonnant"};
        assertArrayEquals(expected, reggex.extractTitles(text, reggex));
    }

    @Test
    void testExtractTitlesWithOneTitle() {
        String text = "Belle-Ile-en-Mer. Un festival de l'humour attirant";
        String[] expected = {"Belle-Ile-en-Mer.", "Un festival de l'humour attirant"};
        assertArrayEquals(expected, reggex.extractTitles(text, reggex));
    }

    @Test
    void testExtractTitlesWithNoMatch() {
        String text = "";
        String[] expected = {"NoTitle"};
        assertArrayEquals(expected, reggex.extractTitles(text, reggex));
    }

    @Test
    void testExtractCityAndPostalCode() {
        String text = "Belle-Ile-en-Mer.\nUn festival de\nl'humour attirant\n56 - Auray\nmardi 21 mai 2024\nAnnonces bonnes affaires";
        String expected = "56 - Auray";
        assertEquals(expected, reggex.extractCityAndPostalCode(text, reggex));
    }

    @Test
    void testExtractCityAndPostalCodeNoMatch() {
        String text = "No city and postal code here";
        String expected = "null";
        assertEquals(expected, reggex.extractCityAndPostalCode(text, reggex));
    }

    @Test
    void testExtractDate() {
        String text = "Belle-Ile-en-Mer.\nUn festival de\nl'humour attirant\n56 - Auray\nmardi 21 mai 2024\nAnnonces bonnes affaires";
        String expected = "mardi 21 mai 2024";
        assertEquals(expected, reggex.extractDate(text, reggex));
    }

    @Test
    void testExtractDateNoMatch() {
        String text = "No date here";
        String expected = "null";
        assertEquals(expected, reggex.extractDate(text, reggex));
    }
}
