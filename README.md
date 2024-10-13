@RunWith(MockitoJUnitRunner.class)
public class IMLineageDataDAOTest {

    @Mock
    private JdbcTemplate jdbcTemplate;  // Step 1: Define the mock

    @InjectMocks
    private IMLineageDataDAO lineageDataDAO;  // Step 2: Inject the mock into the DAO

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

        // Call the method to test
        lineageDataDAO.deleteFileColumn(table);

        // Verify that the necessary database calls were made
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(String.class), anyVararg());
        verify(jdbcTemplate, times(1)).update(anyString(), anyVararg());
    }
}
