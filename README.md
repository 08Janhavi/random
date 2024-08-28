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
