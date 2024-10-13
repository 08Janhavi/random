@Test
void getAllDatabases_ShouldReturnDatabaseList() throws SQLException {
    // Arrange
    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.getString("database_name")).thenReturn("db1").thenReturn("db2").thenReturn("db3");
    
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenAnswer(invocation -> {
        RowMapper<String> rowMapper = invocation.getArgument(1);
        List<String> databases = new ArrayList<>();
        databases.add(rowMapper.mapRow(resultSet, 0));
        databases.add(rowMapper.mapRow(resultSet, 1));
        databases.add(rowMapper.mapRow(resultSet, 2));
        return databases;
    });

    // Act
    List<String> databases = dao.getAllDatabases();

    // Assert
    assertNotNull(databases);
    assertEquals(3, databases.size());
    assertEquals("db1", databases.get(0));
    verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class));
}


@Test
void getAllTables_ShouldReturnTableList() throws SQLException {
    // Arrange
    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.getString("db_table_name")).thenReturn("table1").thenReturn("table2");

    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenAnswer(invocation -> {
        RowMapper<String> rowMapper = invocation.getArgument(1);
        List<String> tables = new ArrayList<>();
        tables.add(rowMapper.mapRow(resultSet, 0));
        tables.add(rowMapper.mapRow(resultSet, 1));
        return tables;
    });

    // Act
    List<String> tables = dao.getAllTables();

    // Assert
    assertNotNull(tables);
    assertEquals(2, tables.size());
    assertEquals("table1", tables.get(0));
    verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class));
}



@Test
void getLineageDataFromDB_ShouldReturnLineageData() throws SQLException {
    // Arrange
    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.getString("database_name")).thenReturn("db1");
    when(resultSet.getString("db_table_name")).thenReturn("table1");
    when(resultSet.getString("db_column_name")).thenReturn("columnName1");
    when(resultSet.getString("process_name")).thenReturn("processName1");
    when(resultSet.getString("file_column_name")).thenReturn("fileColumnName1");
    when(resultSet.getString("file_name")).thenReturn("fileName1");
    when(resultSet.getString("file_source")).thenReturn("fileSource1");

    when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyString())).thenAnswer(invocation -> {
        RowMapper<Table> rowMapper = invocation.getArgument(1);
        List<Table> tables = new ArrayList<>();
        tables.add(rowMapper.mapRow(resultSet, 0));
        return tables;
    });

    // Act
    List<Table> lineageData = dao.getLineageDataFromDB("db1", "table1");

    // Assert
    assertNotNull(lineageData);
    assertEquals(1, lineageData.size());
    verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), anyString(), anyString());
}




@Test
void saveLineageData_ShouldSaveNewTableAndColumns() throws SQLException {
    // Arrange
    Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList())));
    String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
    String insertTableQuery = "INSERT INTO rawdata.dbo.lineage_data_db_tables (database_name, db_table_name, created_by) VALUES (?, ?, ?)";
    String insertColumnQuery = "INSERT INTO rawdata.dbo.lineage_data_db_table_columns (db_table_id, db_column_name, process_name, created_by) VALUES (?, ?, ?, ?)";

    when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn(null).thenReturn("1");
    when(jdbcTemplate.update(insertTableQuery, "db1", "table1", "janhavi")).thenReturn(1);

    // Act
    dao.saveLineageData(table);

    // Assert
    verify(jdbcTemplate, times(2)).queryForObject(findTableQuery, String.class, "db1", "table1");
    verify(jdbcTemplate, times(1)).update(insertTableQuery, "db1", "table1", "janhavi");
    verify(jdbcTemplate, times(1)).update(insertColumnQuery, "1", "column1", "process1", "janhavi");
}




@Test
void deleteFileColumn_ShouldDeleteColumnsAndFileColumns() throws SQLException {
    // Arrange
    Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("fileColumn1", "file1", "source1")))));
    String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
    String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";
    String deleteFileColumnQuery = "DELETE FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ? AND file_column_name = ?";
    String checkFileColumnsQuery = "SELECT COUNT(*) FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ?";

    when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn("1");
    when(jdbcTemplate.queryForObject(findColumnQuery, String.class, "1", "column1")).thenReturn("10");
    when(jdbcTemplate.update(deleteFileColumnQuery, "10", "fileColumn1")).thenReturn(1);
    when(jdbcTemplate.queryForObject(checkFileColumnsQuery, Integer.class, "10")).thenReturn(0);

    // Act
    dao.deleteFileColumn(table);

    // Assert
    verify(jdbcTemplate, times(1)).queryForObject(findTableQuery, String.class, "db1", "table1");
    verify(jdbcTemplate, times(1)).queryForObject(findColumnQuery, String.class, "1", "column1");
    verify(jdbcTemplate, times(1)).update(deleteFileColumnQuery, "10", "fileColumn1");
}
