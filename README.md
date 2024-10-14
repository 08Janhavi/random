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
    verify(jdbcTemplate, times(1)).queryForObject(findTableQuery, String.class, "db1", "table1");
    verify(jdbcTemplate, times(1)).update(insertColumnQuery, "1", "column1", "process1", "janhavi");
}
