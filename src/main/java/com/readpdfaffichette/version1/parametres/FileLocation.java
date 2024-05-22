package com.readpdfaffichette.version1.parametres;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileLocation {
    @Value("${file.link}")
    private String link;

    @Value("${file.saveplace}")
    private String fileSaveplace;

    @Value("${docHtml.saveplace}")
    private String docHtmlSaveplace;
    
    @Value("${styleCss.saveplace}")
    private String styleCssSaveplace;

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

    public String getDocHtmlSaveplace() {
        return docHtmlSaveplace;
    }

    public String getStyleCssSaveplace() {
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
}
