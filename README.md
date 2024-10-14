import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Arrays;

public class IMLineageDataDAOTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private IMLineageDataDAO dao;

    private static final String CREATED_BY = "testUser";

    @Test
    void saveLineageData_ShouldInsertNewTable_WhenTableNotFound() {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("fileColumn1", "fileName1", "fileSource1")))));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn(null); // Table not found
        doNothing().when(jdbcTemplate).update(anyString(), anyString(), anyString(), anyString()); // Inserting new table
        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn("1"); // Fetch newly inserted table id

        // Act
        dao.saveLineageData(table);

        // Assert
        verify(jdbcTemplate, times(1)).update(anyString(), eq("db1"), eq("table1"), eq(CREATED_BY));
    }

    @Test
    void saveLineageData_ShouldUseExistingTable_WhenTableFound() {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("fileColumn1", "fileName1", "fileSource1")))));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn("1"); // Table found

        // Act
        dao.saveLineageData(table);

        // Assert
        verify(jdbcTemplate, never()).update(anyString(), anyString(), anyString(), anyString()); // No table insertion
    }

    @Test
    void saveLineageData_ShouldInsertNewColumn_WhenColumnNotFound() {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("fileColumn1", "fileName1", "fileSource1")))));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
        String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn("1"); // Table found
        when(jdbcTemplate.queryForObject(findColumnQuery, String.class, "1", "column1")).thenReturn(null); // Column not found

        // Act
        dao.saveLineageData(table);

        // Assert
        verify(jdbcTemplate, times(1)).update(anyString(), eq("1"), eq("column1"), eq("process1"), eq(CREATED_BY)); // Insert column
    }

    @Test
    void saveLineageData_ShouldUpdateExistingColumn_WhenColumnFound() {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("fileColumn1", "fileName1", "fileSource1")))));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
        String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn("1"); // Table found
        when(jdbcTemplate.queryForObject(findColumnQuery, String.class, "1", "column1")).thenReturn("1"); // Column found

        // Act
        dao.saveLineageData(table);

        // Assert
        verify(jdbcTemplate, times(1)).update(anyString(), eq("column1"), eq("1")); // Update column
    }

    @Test
    void saveLineageData_ShouldInsertNewFileColumn_WhenFileColumnNotFound() {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("fileColumn1", "fileName1", "fileSource1")))));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
        String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";
        String findFileColumnQuery = "SELECT file_column_id FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ? AND file_column_name = ?";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn("1"); // Table found
        when(jdbcTemplate.queryForObject(findColumnQuery, String.class, "1", "column1")).thenReturn("1"); // Column found
        when(jdbcTemplate.queryForObject(findFileColumnQuery, String.class, "1", "fileColumn1")).thenReturn(null); // File column not found

        // Act
        dao.saveLineageData(table);

        // Assert
        verify(jdbcTemplate, times(1)).update(anyString(), eq("1"), eq("fileColumn1"), eq("fileName1"), eq("fileSource1"), eq(CREATED_BY)); // Insert file column
    }

    @Test
    void saveLineageData_ShouldUpdateExistingFileColumn_WhenFileColumnFound() {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("fileColumn1", "fileName1", "fileSource1")))));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
        String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";
        String findFileColumnQuery = "SELECT file_column_id FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ? AND file_column_name = ?";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn("1"); // Table found
        when(jdbcTemplate.queryForObject(findColumnQuery, String.class, "1", "column1")).thenReturn("1"); // Column found
        when(jdbcTemplate.queryForObject(findFileColumnQuery, String.class, "1", "fileColumn1")).thenReturn("1"); // File column found

        // Act
        dao.saveLineageData(table);

        // Assert
        verify(jdbcTemplate, times(1)).update(anyString(), eq("fileColumn1"), eq("fileName1"), eq("fileSource1"), eq("1")); // Update file column
    }

    @Test
    void saveLineageData_ShouldHandleException_WhenTableQueryFails() {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("fileColumn1", "fileName1", "fileSource1")))));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenThrow(new RuntimeException("Database error")); // Simulate exception

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> dao.saveLineageData(table));
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void saveLineageData_ShouldHandleException_WhenColumnQueryFails() {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("fileColumn1", "fileName1", "fileSource1")))));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
        String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn("1"); // Table found
        when(jdbcTemplate.queryForObject(findColumnQuery, String.class, "1", "column1")).thenThrow(new RuntimeException("Database error")); // Simulate exception

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> dao.saveLineageData(table));
        assertEquals("Database error", exception.getMessage());
    }
}
