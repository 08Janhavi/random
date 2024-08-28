package com.nomura.im.lineage.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.nomura.im.lineage.vo.Table;
import com.nomura.im.lineage.vo.TableColumn;
import com.nomura.im.lineage.vo.FileColumn;

@Repository
public class IMLineageDataDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;


	public List<Table> getLineageDataFromDB() {
		List<Table> lineageData = new ArrayList<>();
		String dbTableQuery="SELECT db_table_id,database_name,db_table_name FROM dbo.lineage_data_db_tables";
		List<Table> dbTables=jdbcTemplate.query(dbTableQuery,new RowMapper<Table>(){
			@Override
			public Table mapRow(ResultSet rs,int rowNum) throws SQLException{
				Table table= new Table(
						rs.getString("db_table_id"),
						rs.getString("database_name"),
						rs.getString("db_table_name"),
						new ArrayList<>()
				);
				return table;
			}
		});

		for(Table dbTable:dbTables) {
			String tableColumnQuery = "SELECT db_column_id,db_column_name,process_name FROM dbo.lineage_data_db_table_columns WHERE db_table_id=?";
			List<TableColumn> tableColumns = jdbcTemplate.query(tableColumnQuery,new Object[]{dbTable.tableId()},  new RowMapper<TableColumn>() {

				@Override
				public TableColumn mapRow(ResultSet rs, int rowNum) throws SQLException {
					TableColumn tableColumn= new TableColumn(
							rs.getString("db_column_id"),
							rs.getString("db_column_name"),
							rs.getString("process_name"),
							new ArrayList<>()
					);
					return tableColumn;
				}
			});


			for (TableColumn tableColumn : tableColumns) {
				String fileColumnQuery = "SELECT file_column_name,file_name,file_source FROM dbo.lineage_data_file_columns WHERE db_column_id=?";
				List<FileColumn> fileColumns = jdbcTemplate.query(fileColumnQuery,new Object[]{tableColumn.columnId()}, new RowMapper<FileColumn>() {
					@Override
					public FileColumn mapRow(ResultSet rs, int rowNum) throws SQLException {
						FileColumn fileColumn= new FileColumn(
								rs.getString("file_column_name"),
								rs.getString("file_name"),
								rs.getString("file_source")
						);
						return fileColumn;
					}
				});

				tableColumn.fileColumns().addAll(fileColumns);
			}
			dbTable.tableColumns().addAll(tableColumns);
		}
		lineageData.addAll(dbTables);

		//TODO: IMPLEMENT YOUR CODE HERE
		return lineageData;
	}
}









package com.nomura.im.lineage.service;

public interface IMLineageGeneratorService {

	public void generateAndPublishLineageData();
	
}
