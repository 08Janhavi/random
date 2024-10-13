@Test
    public void testGetLineageDataFromDB() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString("database_name")).thenReturn("databaseName1");
        when(resultSet.getString("db_table_name")).thenReturn("tableName1");

        when(resultSet.getString("db_column_name")).thenReturn("columnName1");
        when(resultSet.getString("process_name")).thenReturn("processName1");

        when(resultSet.getString("file_column_name")).thenReturn("fileColumnName1");
        when(resultSet.getString("file_name")).thenReturn("fileName1");
        when(resultSet.getString("file_source")).thenReturn("fileSource1");

        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenAnswer(invocation -> {
            RowMapper<Table> rowMapper = invocation.getArgument(1);
            List<Table> tables = new ArrayList<>();
            tables.add(rowMapper.mapRow(resultSet, 0));
            return tables;
        });

        List<Table> lineageData = imLineageDataDAO.getLineageDataFromDB();
        assertNotNull(lineageData);
        assertEquals(1, lineageData.size());
    }
