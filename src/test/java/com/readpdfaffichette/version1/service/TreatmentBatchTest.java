package com.readpdfaffichette.version1.service;

import com.readpdfaffichette.version1.exceptions.CustomAppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TreatmentBatchTest {

    @Mock
    private TreatmentService treatmentService;

    @InjectMocks
    private TreatmentBatch treatmentBatch;

    @BeforeEach
    public void setUp() {
        treatmentBatch = new TreatmentBatch(treatmentService);
    }

    @Test
    @DisplayName("test pour vérifier si la méthode ReadPdf appelle bien une seule fois la méthode readpdf de treatmentService")
    public void testReadPdfSuccess() throws IOException, CustomAppException {

        treatmentBatch.readPdf();

        verify(treatmentService, times(1)).readpdf(null);
    }

    @Test
    @DisplayName("test pour vérifier si la méthode ReadPdf renvoie bien une erreur IOException en cas d'échec")
    public void testReadPdfThrowsIOException() throws IOException, CustomAppException {

        doThrow(IOException.class).when(treatmentService).readpdf(null);

        assertThrows(IOException.class, () -> treatmentBatch.readPdf());
        verify(treatmentService, times(1)).readpdf(null);
    }

    @Test
    @DisplayName("test pour vérifier si la méthode ReadPdf renvoie bien une erreur en cas d'échec")
    public void testReadPdfThrowsCustomAppException() throws IOException, CustomAppException {

        doThrow(CustomAppException.class).when(treatmentService).readpdf(null);

        assertThrows(CustomAppException.class, () -> treatmentBatch.readPdf());
        verify(treatmentService, times(1)).readpdf(null);
    }
}

