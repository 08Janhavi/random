@Test
void testSaveLineageData_NewTable() {
    Table table = new Table("database1", "table1", Arrays.asList(
            new TableColumn("column1", "process1", Arrays.asList(
                    new FileColumn("file_column1", "file1.csv", "source1")
            ))
    ));

    when(jdbcTemplate.queryForObject(anyString(), eq(String.class), any(), any()))
            .thenReturn(null)
            .thenReturn("1");

    lineageDataService.saveLineageData(table);

    verify(jdbcTemplate, times(3)).update(queryCaptor.capture(), argsCaptor.capture());

    List<String> queries = queryCaptor.getAllValues();
    assertEquals("INSERT INTO rawdata.dbo.lineage_data_db_tables (database_name, db_table_name,created_by) VALUES (?, ?,?)", queries.get(0));
    assertEquals("INSERT INTO rawdata.dbo.lineage_data_db_table_columns (db_table_id, db_column_name,process_name,created_by) VALUES (?, ?,?,?)", queries.get(1));
    assertEquals("INSERT INTO rawdata.dbo.lineage_data_file_columns (db_column_id, file_column_name, file_name, file_source,created_by) VALUES (?, ?, ?, ?,?)", queries.get(2));

    List<Object[]> args = argsCaptor.getAllValues();
    assertEquals(Arrays.asList("database1", "table1", "CREATED_BY"), Arrays.asList(args.get(0)));
    assertEquals("1", args.get(1)[0]); // Assert the first argument is a String
    assertEquals(Arrays.asList("column1", "process1", "CREATED_BY"), Arrays.asList((Object[]) args.get(1))); // Cast to Object[] before converting to List
    assertEquals("1", args.get(2)[0]); // Assert the first argument is a String
    assertEquals(Arrays.asList("file_column1", "file1.csv", "source1", "CREATED_BY"), Arrays.asList((Object[]) args.get(2))); // Cast to Object[] before converting to List
}
