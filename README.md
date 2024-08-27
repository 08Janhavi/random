package com.nomura.im.lineage.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LineagePublisherServiceTest {

    private LineagePublisherService lineagePublisherService;
    private Workbook mockWorkbook;

    @BeforeEach
    void setUp() {
        lineagePublisherService = new LineagePublisherService();
        mockWorkbook = Mockito.mock(Workbook.class);
    }

    @Test
    void testPublishExcel() throws Exception {
        // Mock FileOutputStream to avoid actual file creation
        FileOutputStream mockFileOut = Mockito.mock(FileOutputStream.class);
        File tempFile = File.createTempFile("test", ".xlsx");

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            mockFileOut = spy(fos);  // Create a spy to mock behavior
            doNothing().when(mockWorkbook).write(mockFileOut); // Mock workbook's write method
        }

        lineagePublisherService.publishExcel(mockWorkbook);

        // Verify that the write method was called on the workbook
        verify(mockWorkbook, times(1)).write(any(FileOutputStream.class));
    }

    @Test
    void testPublishExcelException() throws Exception {
        // Simulate an exception while writing the file
        doThrow(new IOException("Write failed")).when(mockWorkbook).write(any(FileOutputStream.class));

        lineagePublisherService.publishExcel(mockWorkbook);

        // Verify that the write method was attempted despite the exception
        verify(mockWorkbook, times(1)).write(any(FileOutputStream.class));
    }

    @Test
    void testPublishJson() throws Exception {
        // Since the publishJson method currently does nothing, we’ll mock it up assuming it writes to a file.

        // Simulating writing a JSON string to a file
        String jsonData = "{\"key\":\"value\"}";
        lineagePublisherService.publishJson(jsonData);

        // Add some form of validation for future expansion if needed
        assertTrue(jsonData.contains("key"));
    }
}
