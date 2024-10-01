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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Repository
public class IMLineageDataDAO {

    private static final Logger logger = LogManager.getLogger(IMLineageDataDAO.class.getName());
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Table> getLineageDataFromDB(String databaseName,String tableName) {
        String query = "SELECT t.db_table_id,t.database_name,t.db_table_name, tc.db_column_id,tc.db_column_name,tc.process_name,fc.file_column_name,fc.file_name,fc.file_source FROM rawdata.dbo.lineage_data_db_tables t LEFT JOIN rawdata.dbo.lineage_data_db_table_columns tc ON t.db_table_id=tc.db_table_id LEFT JOIN rawdata.dbo.lineage_data_file_columns fc ON tc.db_column_id=fc.db_column_id ORDER BY t.db_table_name,tc.db_column_name";
        logger.info("Executing query to retrieve lineage data : {}", query);
        LineageDataRowMapper rowMapper = new LineageDataRowMapper();
        jdbcTemplate.query(query, rowMapper);
        return rowMapper.getLineageData();
    }

    private static class LineageDataRowMapper implements RowMapper<Table> {
        private final List<Table> lineageData = new ArrayList<>();
        private Table currentTable;
        private TableColumn currentColumn;

        @Override
        public Table mapRow(ResultSet rs, int rowNum) throws SQLException {
            String tableName = rs.getString("db_table_name");
            logger.debug("Processing table : {}", tableName);

            currentTable = lineageData.stream().filter(t -> t.tableName().equals(tableName)).findFirst().orElse(null);

            if (currentTable == null) {
                logger.info("Creating new table entry for table : {}", tableName);
                currentTable = new Table(rs.getString("database_name"), rs.getString("db_table_name"), new ArrayList<>());
                lineageData.add(currentTable);
            }

            String columnName = rs.getString("db_column_name");
            logger.debug("Processing column : {}", columnName);

            currentColumn = currentTable.tableColumns().stream().filter(c -> c.columnName().equals(columnName)).findFirst().orElse(null);

            if (currentColumn == null) {
                logger.info("Creating new column entry for column : {}", columnName);
                currentColumn = new TableColumn(rs.getString("db_column_name"), rs.getString("process_name"), new ArrayList<>());
                currentTable.tableColumns().add(currentColumn);
            }

            if (rs.getString("file_column_name") != null) {
                String fileColumnName = rs.getString("file_column_name");
                logger.debug("Processing file column : {}", fileColumnName);
                FileColumn fileColumn = new FileColumn(rs.getString("file_column_name"), rs.getString("file_name"), rs.getString("file_source"));
                currentColumn.fileColumns().add(fileColumn);
            }
            return null;
        }

        public List<Table> getLineageData() {
            System.out.println(lineageData);
            return lineageData;
        }
    }
}
