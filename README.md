@Test
    public void testGetLineageDataFromDB() throws SQLException {
       when(jdbcTemplate.query(
                        ArgumentMatchers.anyString(), ArgumentMatchers.any(RowMapper.class)))
                .thenAnswer((invocation) -> {

                    RowMapper<Table> rowMapper = (RowMapper<Table>) invocation.getArgument(1);
                    ResultSet rs = mock(ResultSet.class);

                    // Mock ResultSet to return two rows.
                    when(rs.getString(ArgumentMatchers.eq("db_table_id")))
                            .thenReturn("tableId1");
                    when(rs.getString(ArgumentMatchers.eq("database_name")))
                            .thenReturn("databaseName1");
                    when(rs.getString(ArgumentMatchers.eq("db_table_name")))
                            .thenReturn("");

                    List<Table> tables = new ArrayList<>();
                    tables.add(rowMapper.mapRow(rs, 0));

                    return tables;
                });
    }







org.mockito.exceptions.misusing.UnnecessaryStubbingException: 
Unnecessary stubbings detected.
Clean & maintainable test code requires zero unnecessary code.
Following stubbings are unnecessary (click to navigate to relevant line of code):
  1. -> at com.nomura.im.lineage.dao.IMLineageDataDAOTest.testGetLineageDataFromDB(IMLineageDataDAOTest.java:47)
Please remove unnecessary stubbings or use 'lenient' strictness. More info: javadoc for UnnecessaryStubbingException class.

	at org.mockito.junit.jupiter.MockitoExtension.afterEach(MockitoExtension.java:197)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
