@Test
void saveLineageData_ShouldSaveNewTableAndColumns() {
    // Arrange
    Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("columnName1","fileName1","fileSource1")))));

    String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
    String insertTableQuery = "INSERT INTO rawdata.dbo.lineage_data_db_tables (database_name, db_table_name, created_by) VALUES (?, ?, ?)";
    String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";
    String insertColumnQuery = "INSERT INTO rawdata.dbo.lineage_data_db_table_columns (db_table_id, db_column_name, process_name, created_by) VALUES (?, ?, ?, ?)";
    String findFileColumnQuery = "SELECT file_column_id FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ? AND file_column_name = ?";
    String insertFileColumnQuery = "INSERT INTO rawdata.dbo.lineage_data_file_columns (db_column_id, file_column_name, file_name, file_source,created_by) VALUES (?, ?, ?, ?,?)";

    // Step 1: Mock the table ID to be null initially, then return an ID after insertion
    when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn(null).thenReturn("1");

    // Step 2: Mock the insert for the table and return the new table ID on the second query
    when(jdbcTemplate.update(insertTableQuery, "db1", "table1", "janhavi")).thenReturn(1);

    // Step 3: Mock the column ID to be null initially, then return an ID after insertion
    when(jdbcTemplate.queryForObject(findColumnQuery, String.class, "1", "column1")).thenReturn(null).thenReturn("1");

    // Step 4: Mock the insert for the column
    when(jdbcTemplate.update(insertColumnQuery, "1", "column1", "process1", "janhavi")).thenReturn(1);

    // Step 5: Mock the file column ID to be null initially, then mock the insertion
    when(jdbcTemplate.queryForObject(findFileColumnQuery, String.class, "1", "columnName1")).thenReturn(null);
    when(jdbcTemplate.update(insertFileColumnQuery, "1", "columnName1", "fileName1", "fileSource1", "janhavi")).thenReturn(1);

    // Act
    dao.saveLineageData(table);

    // Assert
    verify(jdbcTemplate, times(2)).queryForObject(findTableQuery, String.class, "db1", "table1");  // Twice: before and after insert
    verify(jdbcTemplate, times(1)).update(insertTableQuery, "db1", "table1", "janhavi");
    verify(jdbcTemplate, times(2)).queryForObject(findColumnQuery, String.class, "1", "column1");  // Twice: before and after insert
    verify(jdbcTemplate, times(1)).update(insertColumnQuery, "1", "column1", "process1", "janhavi");
    verify(jdbcTemplate, times(1)).queryForObject(findFileColumnQuery, String.class, "1", "columnName1");
    verify(jdbcTemplate, times(1)).update(insertFileColumnQuery, "1", "columnName1", "fileName1", "fileSource1", "janhavi");
}
