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
