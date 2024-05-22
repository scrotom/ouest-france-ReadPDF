package com.readpdfaffichette.version1.parametres;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileLocation {
      @Value("${file.link}")
      private String link;

      @Value("${file.name}")
      private File nomFichier;

      @Value("${file.saveplace}")
      private String fileSaveplace;

      @Value("${docHtml.saveplace}")
      private String docHtmlSaveplace;
    
      @Value("${styleCss.saveplace}")
      private String StyleCssSaveplace;

    //constructeur
    public String getLink() {
        return link;
    }

    public File getFichier() {
        return nomFichier;
    }

    public String getSaveplace() {
        return fileSaveplace;
    }

    public String getDocHtmlSaveplace() {
        return docHtmlSaveplace;
    }

    public String getStyleCssSaveplace() {
        return StyleCssSaveplace;
    }
}
