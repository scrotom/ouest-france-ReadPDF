package com.readpdfaffichette.version1.service;

import com.readpdfaffichette.version1.exceptions.CustomAppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.logging.Logger;

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
    public void testReadPdfSuccess() throws IOException, CustomAppException {
        // Act
        treatmentBatch.readPdf();

        // Assert
        verify(treatmentService, times(1)).readpdf(null);
    }

    @Test
    public void testReadPdfThrowsIOException() throws IOException, CustomAppException {
        // Arrange
        doThrow(IOException.class).when(treatmentService).readpdf(null);

        // Act & Assert
        assertThrows(IOException.class, () -> treatmentBatch.readPdf());
        verify(treatmentService, times(1)).readpdf(null);
    }

    @Test
    public void testReadPdfThrowsCustomAppException() throws IOException, CustomAppException {
        // Arrange
        doThrow(CustomAppException.class).when(treatmentService).readpdf(null);

        // Act & Assert
        assertThrows(CustomAppException.class, () -> treatmentBatch.readPdf());
        verify(treatmentService, times(1)).readpdf(null);
    }
}

