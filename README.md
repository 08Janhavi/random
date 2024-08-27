package com.nomura.im.lineage.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;

@Service
public class LineagePublisherService {

	public void publishExcel(Workbook imLineageExcelWorkbook) {
		try(FileOutputStream fileOut=new FileOutputStream("lineageData.xlsx")){
			imLineageExcelWorkbook.write(fileOut);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void publishJson(String imLineageData) {
		
	}
}



package com.nomura.im.lineage.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LineagePublisherServiceTest {

    @Mock
    private Workbook mockWorkbook;

    @Mock
    FileOutputStream mockFileOut;

    @InjectMocks
    private LineagePublisherService lineagePublisherService;

    @Test
    void testPublishExcel() throws Exception {
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
}
