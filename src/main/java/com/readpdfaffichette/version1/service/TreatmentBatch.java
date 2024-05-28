package com.readpdfaffichette.version1.service;

import com.readpdfaffichette.version1.exceptions.CustomAppException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class TreatmentBatch {

    Logger log = Logger.getLogger(TreatmentBatch.class.getName());
    private final TreatmentService treatmentService;

    public TreatmentBatch(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    @Scheduled(fixedRate = 60000)
    public void readPdf() throws IOException, CustomAppException {
        log.info("Reading pdf");
        treatmentService.readpdf(null);
        log.info("Done reading pdf");
    }
}
