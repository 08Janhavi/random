    @Test
    public void testGetColumnMappings() throws Exception {
        Table table = new Table("testDb", "testTable", Collections.emptyList());
        when(lineageDataDAO.getLineageDataFromDB("testDb", "testTable")).thenReturn(Collections.singletonList(table));

        mockMvc.perform(get("/getColumnMappings").param("db", "testDb").param("table", "testTable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dbName").value("testDb"))
                .andExpect(jsonPath("$[0].tableName").value("testTable"));

        verify(lineageDataDAO, times(1)).getLineageDataFromDB("testDb", "testTable");
    }




java.lang.AssertionError: No value at JSON path "$[0].dbName"

	at org.springframework.test.util.JsonPathExpectationsHelper.evaluateJsonPath(JsonPathExpectationsHelper.java:302)
	at org.springframework.test.util.JsonPathExpectationsHelper.assertValue(JsonPathExpectationsHelper.java:99)
	at org.springframework.test.web.servlet.result.JsonPathResultMatchers.lambda$value$2(JsonPathResultMatchers.java:111)
	at org.springframework.test.web.servlet.MockMvc$1.andExpect(MockMvc.java:214)
	at com.nomura.im.lineage.controller.LineageDataControllerTests.testGetColumnMappings(LineageDataControllerTests.java:84)
	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
Caused by: com.jayway.jsonpath.PathNotFoundException: No results for path: $[0]['dbName']
