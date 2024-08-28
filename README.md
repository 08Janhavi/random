String dbTableQuery="SELECT db_table_id,database_name,db_table_name FROM dbo.lineage_data_db_tables";
		List<Table> dbTables=jdbcTemplate.query(dbTableQuery,new RowMapper<Table>(){
			@Override
			public Table mapRow(ResultSet rs,int rowNum) throws SQLException{
				Table table= new Table(
						rs.getString("db_table_id"),
						rs.getString("database_name"),
						rs.getString("db_table_name"),
						new ArrayList<>()
				);
				return table;
			}
		});






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
                            .thenReturn("dbTableName1");
                    when(new ArrayList<>()).thenReturn(new ArrayList<>());
                    List<Table> tables = new ArrayList<>();
                    tables.add(rowMapper.mapRow(rs, 0));

                    return tables;
                });










org.mockito.exceptions.misusing.PotentialStubbingProblem: 
Strict stubbing argument mismatch. Please check:
 - this invocation of 'getString' method:
    resultSet.getString("db_table_id");
    -> at com.nomura.im.lineage.dao.IMLineageDataDAO$1.mapRow(IMLineageDataDAO.java:31)
 - has following stubbing(s) with different arguments:
    1. resultSet.getString(null);
      -> at com.nomura.im.lineage.dao.IMLineageDataDAOTest.lambda$testGetLineageDataFromDB$1(IMLineageDataDAOTest.java:77)
    2. resultSet.getString(null);
      -> at com.nomura.im.lineage.dao.IMLineageDataDAOTest.lambda$testGetLineageDataFromDB$1(IMLineageDataDAOTest.java:79)
    3. resultSet.getString(null);
      -> at com.nomura.im.lineage.dao.IMLineageDataDAOTest.lambda$testGetLineageDataFromDB$1(IMLineageDataDAOTest.java:81)
Typically, stubbing argument mismatch indicates user mistake when writing tests.
Mockito fails early so that you can debug potential problem easily.
However, there are legit scenarios when this exception generates false negative signal:
  - stubbing the same method multiple times using 'given().will()' or 'when().then()' API
    Please use 'will().given()' or 'doReturn().when()' API for stubbing.
  - stubbed method is intentionally invoked with different arguments by code under test
    Please use default or 'silent' JUnit Rule (equivalent of Strictness.LENIENT).
For more information see javadoc for PotentialStubbingProblem class.

	at com.nomura.im.lineage.dao.IMLineageDataDAO$1.mapRow(IMLineageDataDAO.java:31)
	at com.nomura.im.lineage.dao.IMLineageDataDAO$1.mapRow(IMLineageDataDAO.java:27)
	at com.nomura.im.lineage.dao.IMLineageDataDAOTest.lambda$testGetLineageDataFromDB$1(IMLineageDataDAOTest.java:85)
	at org.springframework.jdbc.core.JdbcTemplate.query(JdbcTemplate.java:486)
	at com.nomura.im.lineage.dao.IMLineageDataDAO.getLineageDataFromDB(IMLineageDataDAO.java:27)
	at com.nomura.im.lineage.dao.IMLineageDataDAOTest.testGetLineageDataFromDB(IMLineageDataDAOTest.java:89)
	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)


Process finished with exit code -1

