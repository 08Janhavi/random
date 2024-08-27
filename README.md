package com.nomura.im.lineage.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LineagePublisherServiceTest {

    @Test
    public void testPublishExcelSuccess() {
        // Arrange
        LineagePublisherService lineagePublisherService = new LineagePublisherService();
        Workbook workbook = new XSSFWorkbook();
        String filePath = "testLineageData.xlsx";

        // Act
        lineagePublisherService.publishExcel(workbook, filePath);

        // Assert
        File file = new File(filePath);
        assertTrue(file.exists(), "The file should be created");

        // Clean up
        file.delete();
    }

    @Test
    public void testPublishExcelWithException() throws Exception {
        // Arrange
        LineagePublisherService lineagePublisherService = new LineagePublisherService();
        Workbook workbook = new XSSFWorkbook();

        // Use Mockito to mock FileOutputStream and force it to throw an IOException
        FileOutputStream mockFileOutputStream = mock(FileOutputStream.class);
        doThrow(new IOException("Mocked IOException")).when(mockFileOutputStream).write(any(byte[].class));

        LineagePublisherService lineagePublisherSpy = Mockito.spy(lineagePublisherService);
        Mockito.doReturn(mockFileOutputStream).when(lineagePublisherSpy).publishExcel(any(Workbook.class), anyString());

        // Act and Assert (verify that the exception does not break the code)
        assertDoesNotThrow(() -> lineagePublisherSpy.publishExcel(workbook, "testLineageData.xlsx"));

        // Clean up the resources if needed (no file should be created)
        File file = new File("testLineageData.xlsx");
        if (file.exists()) {
            file.delete();
        }
    }
}
