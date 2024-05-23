package com.readpdfaffichette.version1.parametres;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileLocation {
    @Value("${inputfile.link}")
    private String link;

    @Value("${file.saveplace}")
    private String fileSaveplace;

    @Value("${docHtml.saveplace}")
    private Path docHtmlSaveplace;
    
    @Value("${styleCss.saveplace}")
    private Path styleCssSaveplace;

    @Value("${styleCss.source}")
    private Path styleCssSource;

    //récupération des fichiers pour le doc html final
    @Value("${partie1.source}")
    private Path textFile1;

    @Value("${partie3.source}")
    private Path textFile3;

    @Value("${partie1.saveplace}")
    private Path textFile1Saveplace;

    //récupération du dossier d'enregistrement des document mergé
    @Value("${mergedFile.saveplace}")
    private Path mergedFile;

    //récupération des reggex
    @Value("${reggex.titles}")
    private String reggexTitles;

    @Value("${reggex.city}")
    private String reggexCity;

    @Value("${reggex.date}")
    private String reggexDate;

    //constructeur
    public String getLink() {
        return link;
    }

    public String getSaveplace() {
        return fileSaveplace;
    }

    public Path getDocHtmlSaveplace() {
        return docHtmlSaveplace;
    }

    public Path getStyleCssSaveplace() {
        return styleCssSaveplace;
    }

    public String getReggexTitles() {
        return reggexTitles;
    }

    public String getReggexCity() {
        return reggexCity;
    }

    public String getReggexDate() {
        return reggexDate;
    }

    public Path getStyleCssSource() {
        return styleCssSource;
    }

    public Path getTextFile1() {
        return textFile1;
    }

    public Path getTextFile3() {
        return textFile3;
    }

    public Path getMergedFile() {
        return mergedFile;
    }

    public Path getTextFile1Saveplace() {
        return textFile1Saveplace;
    }
}
