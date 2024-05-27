/*
 * Nom         : Version1Application.java
 *
 * Description : Application permettant d'effectuer les opérations dans le but de scanner des affichettes au format pdf, et d'en extraire le contenu trié dans un doc html.
 *
 * Date        : 23/05/2024
 * 
 */

 package com.readpdfaffichette.version1;

 import java.io.IOException;

 import org.springframework.boot.autoconfigure.SpringBootApplication;

 import com.readpdfaffichette.version1.exceptions.CustomAppException;
 import com.readpdfaffichette.version1.treatment.Treatment;
 
 @SpringBootApplication
 public class ReadPDF {
 
     public static void main(String[] args) throws IOException, CustomAppException {
         Treatment treatment = new Treatment();   
         treatment.readpdf(args);
     }
 }