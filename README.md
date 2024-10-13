 @Test
    public void testDeleteFileColumn() throws Exception {
        Table table = new Table("testDb", "testTable", Collections.emptyList());

        mockMvc.perform(delete("/deleteFileColumn")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dbName\": \"testDb\", \"tableName\": \"testTable\", \"tableColumns\": []}")
        ).andExpect(status().isOk());

        verify(lineageDataDAO, times(1)).deleteFileColumn(any(Table.class));
    }

Table[databaseName=null, tableName=testTable, tableColumns=[]]

java.lang.AssertionError: Status expected:<200> but was:<400>
Expected :200
Actual   :400
<Click to see difference>


	at org.springframework.test.util.AssertionErrors.fail(AssertionErrors.java:59)
	at org.springframework.test.util.AssertionErrors.assertEquals(AssertionErrors.java:122)
	at org.springframework.test.web.servlet.result.StatusResultMatchers.lambda$matcher$9(StatusResultMatchers.java:637)
	at org.springframework.test.web.servlet.MockMvc$1.andExpect(MockMvc.java:214)
	at com.nomura.im.lineage.controller.LineageDataControllerTests.testDeleteFileColumn(LineageDataControllerTests.java:108)
	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)


Process finished with exit code -1
