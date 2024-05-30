/*
 * Nom         : ReadPDF.java
 *
 * Description : Application permettant d'effectuer les opérations dans le but de scanner des affichettes au format pdf, et d'en extraire le contenu trié dans un doc html.
 *
 * Date        : 23/05/2024
 * 
 */

 package com.readpdfaffichette.version1;

 import java.io.IOException;

 import org.springframework.boot.SpringApplication;
 import org.springframework.boot.autoconfigure.SpringBootApplication;

 import com.readpdfaffichette.version1.exceptions.CustomAppException;


@SpringBootApplication
 public class ReadPDF {

     //execution du traitement
     public static void main(String[] args) throws IOException, CustomAppException {
         SpringApplication.run(ReadPDF.class, args);
     }
 }