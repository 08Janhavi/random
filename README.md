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

        dao.saveLineageData(table);

        verify(jdbcTemplate, times(3)).update(queryCaptor.capture(), argsCaptor.capture());

        List<String> queries = queryCaptor.getAllValues();
        assertEquals("INSERT INTO rawdata.dbo.lineage_data_db_tables (database_name, db_table_name,created_by) VALUES (?, ?,?)", queries.get(0));
        assertEquals("UPDATE rawdata.dbo.lineage_data_db_table_columns SET db_column_name = ? WHERE db_column_id = ?", queries.get(1));

        List<Object[]> args = argsCaptor.getAllValues();
        assertEquals(Arrays.asList("database1", "table1", "CREATED_BY"), Arrays.asList(args.get(0)));
        assertEquals(Arrays.asList("1", "column1", "process1", "CREATED_BY"), Arrays.asList(args.get(1)));
    }







java.lang.ClassCastException: class java.lang.String cannot be cast to class [Ljava.lang.Object; (java.lang.String and [Ljava.lang.Object; are in module java.base of loader 'bootstrap')

	at com.nomura.im.lineage.dao.IMLineageDataDAOTests.testSaveLineageData_NewTable(IMLineageDataDAOTests.java:121)
	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
