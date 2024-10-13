@Test
public void testDeleteFileColumn() throws Exception {
    // Setup: Create a mock Table with columns and file columns
    List<FileColumn> fileColumns = Collections.singletonList(new FileColumn("fileColumn1", "fileName1", "fileSource1"));
    List<TableColumn> tableColumns = Collections.singletonList(new TableColumn("dbColumn1", "process1", fileColumns));
    Table table = new Table("testDb", "testTable", tableColumns);

    // Mock the necessary database interactions
    when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyVararg()))
            .thenReturn("mockedDbTableId", "mockedDbColumnId", "mockedFileColumnId");

    when(jdbcTemplate.update(anyString(), anyVararg())).thenReturn(1); // Simulate successful deletion

    // Perform the delete request
    mockMvc.perform(delete("/deleteFileColumn")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"databaseName\": \"testDb\", \"tableName\": \"testTable\", \"tableColumns\": [{\"columnName\": \"dbColumn1\", \"fileColumns\": [{\"columnName\": \"fileColumn1\"}]}]}")
    ).andExpect(status().isOk());

    // Verify that the DAO method was called
    verify(lineageDataDAO, times(1)).deleteFileColumn(any(Table.class));

    // Optionally, verify database interactions
    verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(String.class), anyVararg());  // Ensure table/column queries were made
    verify(jdbcTemplate, times(1)).update(anyString(), anyVararg());  // Ensure update (delete) was executed
}
