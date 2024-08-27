package com.nomura.im.lineage.service;

import com.nomura.im.lineage.dao.IMLineageDataDAO;
import com.nomura.im.lineage.vo.FileColumn;
import com.nomura.im.lineage.vo.Table;
import com.nomura.im.lineage.vo.TableColumn;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
public class ExcelServiceTest {

    @Mock
    private LineagePublisherService lineagePublisher;

    @Mock
    private IMLineageDataDAO imLineageDao;

    @InjectMocks
    private ExcelService excelService;

    public ExcelServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateAndPublishLineageData() {
        // Arrange
        List<Table> lineageData = new ArrayList<>();
        Table table = new Table();
        TableColumn tableColumn = new TableColumn();
        FileColumn fileColumn = new FileColumn();
        fileColumn.setFileSource("source");
        fileColumn.setFileName("fileName");
        fileColumn.setColumnName("columnName");
        tableColumn.setFileColumns(List.of(fileColumn));
        tableColumn.setProcessName("processName");
        table.setTableColumns(List.of(tableColumn));
        table.setDatabaseName("dbName");
        table.setTableName("tableName");
        lineageData.add(table);

        when(imLineageDao.getLineageDataFromDB()).thenReturn(lineageData);

        // Act
        excelService.generateAndPublishLineageData();

        // Assert
        verify(imLineageDao).getLineageDataFromDB();
        verify(lineagePublisher).publishExcel(any(Workbook.class));
    }
}





package com.nomura.im.lineage.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LineagePublisherServiceTest {

    @Test
    public void testPublishExcel() {
        // Arrange
        LineagePublisherService lineagePublisherService = new LineagePublisherService();
        Workbook workbook = new XSSFWorkbook();
        String filePath = "testLineageData.xlsx";

        // Act
        lineagePublisherService.publishExcel(workbook);

        // Assert
        File file = new File(filePath);
        assertTrue(file.exists(), "The file should be created");

        // Clean up
        file.delete();
    }
}

