/*
 * Nom         : TreatementBatch.java
 *
 * Description : Classe permettant de démarrer le traitement au lancement de l'application
 *
 * Date        : 23/05/2024
 *
 */

package com.readpdfaffichette.version1.service;

import com.readpdfaffichette.version1.exceptions.CustomAppException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
@Log4j2
public class TreatmentBatch {

    Logger log = Logger.getLogger(TreatmentBatch.class.getName());
    private final TreatmentService treatmentService;

    public TreatmentBatch(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void readPdf() throws IOException, CustomAppException {
        log.info("lecture des pdf");
        treatmentService.readpdf(null);
        log.info("traitement fini");
    }
}
