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
				return new Table(
						rs.getString("database_name"),
						rs.getString("db_table_name"),
						new ArrayList<>()
				);
			}
		});

		for(Table dbTable:dbTables) {
			String tableColumnQuery = "SELECT db_column_id,db_column_name,process_name FROM dbo.lineage_data_db_table_columns where db_table_id IN (SELECT db_table_id FROM dbo.lineage_data_db_tables) ";
			List<TableColumn> tableColumns = jdbcTemplate.query(tableColumnQuery, new Object[]{dbTable.tableName()}, new RowMapper<TableColumn>() {
				@Override
				public TableColumn mapRow(ResultSet rs, int rowNum) throws SQLException {
					return new TableColumn(
							rs.getString("db_column_name"),
							rs.getString("process_name"),
							new ArrayList<>()
					);
				}
			});

			for (TableColumn tableColumn : tableColumns) {
				String fileColumnQuery = "SELECT file_column_name,file_name,file_source FROM dbo.lineage_data_file_columns where db_column_id IN (SELECT db_column_id from dbo.lineage_data_db_table_columns)";
				List<FileColumn> fileColumns = jdbcTemplate.query(fileColumnQuery, new Object[]{tableColumn.columnName()}, new RowMapper<FileColumn>() {
					@Override
					public FileColumn mapRow(ResultSet rs, int rowNum) throws SQLException {
						return new FileColumn(
								rs.getString("file_column_name"),
								rs.getString("file_name"),
								rs.getString("file_source")
						);
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
