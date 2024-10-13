import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Arrays;
import java.util.List;

class IMLineageDataDAOTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private IMLineageDataDAO dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllDatabases_ShouldReturnDatabaseList() throws Exception {
        // Arrange
        String query = "SELECT DISTINCT database_name FROM rawdata.dbo.lineage_data_db_tables";
        List<String> mockResult = Arrays.asList("db1", "db2", "db3");
        when(jdbcTemplate.queryForList(query, String.class)).thenReturn(mockResult);

        // Act
        List<String> databases = dao.getAllDatabases();

        // Assert
        assertNotNull(databases);
        assertEquals(3, databases.size());
        assertEquals("db1", databases.get(0));
        verify(jdbcTemplate, times(1)).queryForList(query, String.class);
    }

    @Test
    void getAllTables_ShouldReturnTableList() throws Exception {
        // Arrange
        String query = "SELECT DISTINCT db_table_name FROM rawdata.dbo.lineage_data_db_tables";
        List<String> mockResult = Arrays.asList("table1", "table2");
        when(jdbcTemplate.queryForList(query, String.class)).thenReturn(mockResult);

        // Act
        List<String> tables = dao.getAllTables();

        // Assert
        assertNotNull(tables);
        assertEquals(2, tables.size());
        assertEquals("table1", tables.get(0));
        verify(jdbcTemplate, times(1)).queryForList(query, String.class);
    }

    @Test
    void getLineageDataFromDB_ShouldReturnLineageData() {
        // Arrange
        String databaseName = "db1";
        String tableName = "table1";
        String query = "SELECT t.db_table_id, t.database_name, t.db_table_name, tc.db_column_id, tc.db_column_name, tc.process_name, " +
                "fc.file_column_name, fc.file_name, fc.file_source " +
                "FROM rawdata.dbo.lineage_data_db_tables t " +
                "LEFT JOIN rawdata.dbo.lineage_data_db_table_columns tc ON t.db_table_id = tc.db_table_id " +
                "LEFT JOIN rawdata.dbo.lineage_data_file_columns fc ON tc.db_column_id = fc.db_column_id " +
                "WHERE  t.database_name=? AND t.db_table_name=?" +
                "ORDER BY t.db_table_name, tc.db_column_name";

        // Mock the result set for the query
        when(jdbcTemplate.query(eq(query), any(LineageDataRowMapper.class), eq(databaseName), eq(tableName)))
                .thenAnswer(invocation -> {
                    LineageDataRowMapper rowMapper = invocation.getArgument(1);
                    // Simulate a ResultSet object being processed by the mapper
                    rowMapper.mapRow(mockResultSetForTableData(), 1);
                    return rowMapper.getLineageData();
                });

        // Act
        List<Table> lineageData = dao.getLineageDataFromDB(databaseName, tableName);

        // Assert
        assertNotNull(lineageData);
        verify(jdbcTemplate, times(1)).query(eq(query), any(LineageDataRowMapper.class), eq(databaseName), eq(tableName));
    }

    @Test
    void saveLineageData_ShouldSaveNewTableAndColumns() {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList())));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
        String insertTableQuery = "INSERT INTO rawdata.dbo.lineage_data_db_tables (database_name, db_table_name, created_by) VALUES (?, ?, ?)";
        String insertColumnQuery = "INSERT INTO rawdata.dbo.lineage_data_db_table_columns (db_table_id, db_column_name, process_name, created_by) VALUES (?, ?, ?, ?)";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn(null);
        when(jdbcTemplate.update(insertTableQuery, "db1", "table1", "janhavi")).thenReturn(1);
        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn("1");

        // Act
        dao.saveLineageData(table);

        // Assert
        verify(jdbcTemplate, times(2)).queryForObject(findTableQuery, String.class, "db1", "table1");
        verify(jdbcTemplate, times(1)).update(insertTableQuery, "db1", "table1", "janhavi");
        verify(jdbcTemplate, times(1)).update(insertColumnQuery, "1", "column1", "process1", "janhavi");
    }

    @Test
    void deleteFileColumn_ShouldDeleteColumnsAndFileColumns() throws Exception {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("fileColumn1", "file1", "source1")))));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
        String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";
        String deleteFileColumnQuery = "DELETE FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ? AND file_column_name = ?";
        String checkFileColumnsQuery = "SELECT COUNT(*) FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ?";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn("1");
        when(jdbcTemplate.queryForObject(findColumnQuery, String.class, "1", "column1")).thenReturn("10");
        when(jdbcTemplate.update(deleteFileColumnQuery, "10", "fileColumn1")).thenReturn(1);
        when(jdbcTemplate.queryForObject(checkFileColumnsQuery, Integer.class, "10")).thenReturn(0);

        // Act
        dao.deleteFileColumn(table);

        // Assert
        verify(jdbcTemplate, times(1)).queryForObject(findTableQuery, String.class, "db1", "table1");
        verify(jdbcTemplate, times(1)).queryForObject(findColumnQuery, String.class, "1", "column1");
        verify(jdbcTemplate, times(1)).update(deleteFileColumnQuery, "10", "fileColumn1");
    }
}
