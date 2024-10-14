public void saveLineageData(Table table) {
        logger.info("Saving lineage data for table: {}", table.tableName());

        // Step 1: Get the db_table_id from lineage_data_db_table
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
        String dbTableId = jdbcTemplate.queryForObject(findTableQuery, String.class, table.databaseName(), table.tableName());

        if (dbTableId == null) {
            // Insert the new table if it doesn't exist
            String insertTableQuery = "INSERT INTO rawdata.dbo.lineage_data_db_tables (database_name, db_table_name,created_by) VALUES (?, ?,?)";
            jdbcTemplate.update(insertTableQuery, table.databaseName(), table.tableName(),CREATED_BY);

            // Fetch the newly inserted db_table_id
            dbTableId = jdbcTemplate.queryForObject(findTableQuery, String.class, table.databaseName(), table.tableName(),CREATED_BY);
        }

        // Step 2: For each table column, insert or update data in lineage_data_db_table_columns
        for (TableColumn column : table.tableColumns()) {
            logger.info("Saving column: {}", column.columnName());

            // Check if the column already exists by fetching db_column_id
            String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";
            String dbColumnId = null;
            try{
                dbColumnId = jdbcTemplate.queryForObject(findColumnQuery, String.class, dbTableId, column.columnName());
            }
            catch(Exception e){
                e.printStackTrace();
            }

            if (dbColumnId == null) {
                // Insert a new column if it doesn't exist
                String insertColumnQuery = "INSERT INTO rawdata.dbo.lineage_data_db_table_columns (db_table_id, db_column_name,process_name,created_by) VALUES (?, ?,?,?)";
                jdbcTemplate.update(insertColumnQuery, dbTableId, column.columnName(),column.processName(),CREATED_BY);

                // Fetch the newly inserted db_column_id
                dbColumnId = jdbcTemplate.queryForObject(findColumnQuery, String.class, dbTableId, column.columnName(),column.processName(),CREATED_BY);
            } else {
                // Update the existing column
                String updateColumnQuery = "UPDATE rawdata.dbo.lineage_data_db_table_columns SET db_column_name = ? WHERE db_column_id = ?";
                jdbcTemplate.update(updateColumnQuery, column.columnName(), dbColumnId);
            }

            // Step 3: For each file column, insert or update data in lineage_data_file_columns
            for (FileColumn fileColumn : column.fileColumns()) {
                logger.info("Saving file column: {}", fileColumn.columnName());

                // Check if the file column already exists
                String findFileColumnQuery = "SELECT file_column_id FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ? AND file_column_name = ?";
                String fileColumnId = null;
                try{
                    fileColumnId = jdbcTemplate.queryForObject(findFileColumnQuery, String.class, dbColumnId, fileColumn.columnName());
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                if (fileColumnId == null) {
                    // Insert new file column
                    String insertFileColumnQuery = "INSERT INTO rawdata.dbo.lineage_data_file_columns (db_column_id, file_column_name, file_name, file_source,created_by) VALUES (?, ?, ?, ?,?)";
                    jdbcTemplate.update(insertFileColumnQuery, dbColumnId, fileColumn.columnName(), fileColumn.fileName(), fileColumn.fileSource(),CREATED_BY);
                } else {
                    // Update the existing file column
                    String updateFileColumnQuery = "UPDATE rawdata.dbo.lineage_data_file_columns SET file_column_name=?,file_name = ?, file_source = ? WHERE file_column_id = ?";
                    jdbcTemplate.update(updateFileColumnQuery,fileColumn.columnName(), fileColumn.fileName(), fileColumn.fileSource(), fileColumnId);
                }
            }
        }
    }










@Test
    void saveLineageData_ShouldSaveNewTableAndColumns() {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("columnName1","fileName1","fileSource1")))));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
        String insertTableQuery = "INSERT INTO rawdata.dbo.lineage_data_db_tables (database_name, db_table_name, created_by) VALUES (?, ?, ?)";
        String insertColumnQuery = "INSERT INTO rawdata.dbo.lineage_data_db_table_columns (db_table_id, db_column_name, process_name, created_by) VALUES (?, ?, ?, ?)";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn(null);
        when(jdbcTemplate.update(insertTableQuery, "db1", "table1", "janhavi")).thenReturn(1);
        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn("1");

        // Act
        dao.saveLineageData(table);

        // Assert
        verify(jdbcTemplate, times(1)).queryForObject(findTableQuery, String.class, "db1", "table1");
        verify(jdbcTemplate, times(1)).update(insertTableQuery, "db1", "table1", "janhavi");
        verify(jdbcTemplate, times(1)).update(insertColumnQuery, "1", "column1", "process1", "janhavi");
    }








Argument(s) are different! Wanted:
jdbcTemplate.update(
    "INSERT INTO rawdata.dbo.lineage_data_db_tables (database_name, db_table_name, created_by) VALUES (?, ?, ?)",
    "db1",
    "table1",
    "janhavi"
);
-> at org.springframework.jdbc.core.JdbcTemplate.update(JdbcTemplate.java:1024)
Actual invocations have different arguments:
jdbcTemplate.queryForObject(
    "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?",
    class java.lang.String,
    "db1",
    "table1"
);
-> at com.nomura.im.lineage.dao.IMLineageDataDAO.saveLineageData(IMLineageDataDAO.java:100)
jdbcTemplate.queryForObject(
    "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?",
    class java.lang.String,
    "1",
    "column1"
);
-> at com.nomura.im.lineage.dao.IMLineageDataDAO.saveLineageData(IMLineageDataDAO.java:119)
jdbcTemplate.update(
    "INSERT INTO rawdata.dbo.lineage_data_db_table_columns (db_table_id, db_column_name,process_name,created_by) VALUES (?, ?,?,?)",
    "1",
    "column1",
    "process1",
    "janhavi"
);
-> at com.nomura.im.lineage.dao.IMLineageDataDAO.saveLineageData(IMLineageDataDAO.java:128)
jdbcTemplate.queryForObject(
    "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?",
    class java.lang.String,
    "1",
    "column1",
    "process1",
    "janhavi"
);
-> at com.nomura.im.lineage.dao.IMLineageDataDAO.saveLineageData(IMLineageDataDAO.java:131)
jdbcTemplate.queryForObject(
    "SELECT file_column_id FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ? AND file_column_name = ?",
    class java.lang.String,
    null,
    "columnName1"
);
-> at com.nomura.im.lineage.dao.IMLineageDataDAO.saveLineageData(IMLineageDataDAO.java:146)
jdbcTemplate.update(
    "INSERT INTO rawdata.dbo.lineage_data_file_columns (db_column_id, file_column_name, file_name, file_source,created_by) VALUES (?, ?, ?, ?,?)",
    null,
    "columnName1",
    "fileName1",
    "fileSource1",
    "janhavi"
);
-> at com.nomura.im.lineage.dao.IMLineageDataDAO.saveLineageData(IMLineageDataDAO.java:155)

Comparison Failure: 
<Click to see difference>

Argument(s) are different! Wanted:
jdbcTemplate.update(
    "INSERT INTO rawdata.dbo.lineage_data_db_tables (database_name, db_table_name, created_by) VALUES (?, ?, ?)",
    "db1",
    "table1",
    "janhavi"
);
-> at org.springframework.jdbc.core.JdbcTemplate.update(JdbcTemplate.java:1024)
Actual invocations have different arguments:
jdbcTemplate.queryForObject(
    "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?",
    class java.lang.String,
    "db1",
    "table1"
);
-> at com.nomura.im.lineage.dao.IMLineageDataDAO.saveLineageData(IMLineageDataDAO.java:100)
jdbcTemplate.queryForObject(
    "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?",
    class java.lang.String,
    "1",
    "column1"
);
-> at com.nomura.im.lineage.dao.IMLineageDataDAO.saveLineageData(IMLineageDataDAO.java:119)
jdbcTemplate.update(
    "INSERT INTO rawdata.dbo.lineage_data_db_table_columns (db_table_id, db_column_name,process_name,created_by) VALUES (?, ?,?,?)",
    "1",
    "column1",
    "process1",
    "janhavi"
);
-> at com.nomura.im.lineage.dao.IMLineageDataDAO.saveLineageData(IMLineageDataDAO.java:128)
jdbcTemplate.queryForObject(
    "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?",
    class java.lang.String,
    "1",
    "column1",
    "process1",
    "janhavi"
);
-> at com.nomura.im.lineage.dao.IMLineageDataDAO.saveLineageData(IMLineageDataDAO.java:131)
jdbcTemplate.queryForObject(
    "SELECT file_column_id FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ? AND file_column_name = ?",
    class java.lang.String,
    null,
    "columnName1"
);
-> at com.nomura.im.lineage.dao.IMLineageDataDAO.saveLineageData(IMLineageDataDAO.java:146)
jdbcTemplate.update(
    "INSERT INTO rawdata.dbo.lineage_data_file_columns (db_column_id, file_column_name, file_name, file_source,created_by) VALUES (?, ?, ?, ?,?)",
    null,
    "columnName1",
    "fileName1",
    "fileSource1",
    "janhavi"
);
-> at com.nomura.im.lineage.dao.IMLineageDataDAO.saveLineageData(IMLineageDataDAO.java:155)

	at org.springframework.jdbc.core.JdbcTemplate.update(JdbcTemplate.java:1024)
	at com.nomura.im.lineage.dao.IMLineageDataDAOTests.saveLineageData_ShouldSaveNewTableAndColumns(IMLineageDataDAOTests.java:114)

	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)


Process finished with exit code -1

