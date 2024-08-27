package com.nomura.im.lineage.service;

import java.util.List;

import com.nomura.im.lineage.vo.FileColumn;
import com.nomura.im.lineage.vo.TableColumn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nomura.im.lineage.dao.IMLineageDataDAO;
import com.nomura.im.lineage.vo.Table;

@Service("excel")
public class ExcelService implements IMLineageGeneratorService {

	@Autowired
	private LineagePublisherService lineagePublisher;
	
	@Autowired
	private IMLineageDataDAO imLineageDao;
	
	@Override
	public void generateAndPublishLineageData() {
		List<Table> lineageData = imLineageDao.getLineageDataFromDB();
		
		Workbook excelWorkbook = generateLineageExcel(lineageData);
		
		lineagePublisher.publishExcel(excelWorkbook);
	}
	
	private Workbook generateLineageExcel(List<Table> lineageData) {
		
		Workbook excelWorkbook = new XSSFWorkbook();
		
		//TODO: IMPLEMENT YOUR CODE HERE
		Sheet sheet=excelWorkbook.createSheet("Lineage Data");
		String[] headers={"Process Name","Process Owner","From Namespace","From Table","From Field","To Namespace","To Table","To Field"};
		Row headerRow=sheet.createRow(0);
		for(int i=0;i< headers.length;i++){
			Cell cell=headerRow.createCell(i);
			cell.setCellValue(headers[i]);
		}

		int rowIdx=1;

		for(Table table:lineageData){
			for (TableColumn tableColumn:table.tableColumns()){
				for (FileColumn fileColumn:tableColumn.fileColumns()){
					Row row=sheet.createRow(rowIdx++);
					row.createCell(0).setCellValue(tableColumn.processName());
					row.createCell(1).setCellValue("Instrument Master");
					row.createCell(2).setCellValue(fileColumn.fileSource());
					row.createCell(3).setCellValue(fileColumn.fileName());
					row.createCell(4).setCellValue(fileColumn.columnName());
					row.createCell(5).setCellValue(table.databaseName());
					row.createCell(6).setCellValue(table.tableName());
					row.createCell(7).setCellValue(tableColumn.columnName());
				}
			}
		}

		for(int i=0;i< headers.length;i++){
			sheet.autoSizeColumn(i);
		}
		
		return excelWorkbook;
	}	
}



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
