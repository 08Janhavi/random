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

    @Test
    void deleteFileColumn_ShouldThrowException_WhenTableNotFound() {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("fileColumn1", "fileName1", "fileSource1")))));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            dao.deleteFileColumn(table);
        });

        assertEquals("Table not found", exception.getMessage());
    }

    @Test
    void deleteFileColumn_ShouldThrowException_WhenColumnNotFound() {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("fileColumn1", "fileName1", "fileSource1")))));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
        String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn("1");
        when(jdbcTemplate.queryForObject(findColumnQuery, String.class, "1", "column1")).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            dao.deleteFileColumn(table);
        });

        assertEquals("Column not found for column name: column1", exception.getMessage());
    }

    @Test
    void deleteFileColumn_ShouldThrowException_WhenFileColumnDeletionFails() {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("fileColumn1", "fileName1", "fileSource1")))));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
        String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";
        String deleteFileColumnQuery = "DELETE FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ? AND file_column_name = ?";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn("1");
        when(jdbcTemplate.queryForObject(findColumnQuery, String.class, "1", "column1")).thenReturn("1");
        when(jdbcTemplate.update(deleteFileColumnQuery, "1", "fileColumn1")).thenReturn(0); // Simulate failure to delete

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            dao.deleteFileColumn(table);
        });

        assertEquals("Failed to delete file column: fileColumn1", exception.getMessage());
    }

    @Test
    void deleteFileColumn_ShouldThrowException_WhenDbColumnDeletionFails() {
        // Arrange
        Table table = new Table("db1", "table1", Arrays.asList(new TableColumn("column1", "process1", Arrays.asList(new FileColumn("fileColumn1", "fileName1", "fileSource1")))));
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
        String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";
        String deleteFileColumnQuery = "DELETE FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ? AND file_column_name = ?";
        String checkFileColumnsQuery = "SELECT COUNT(*) FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ?";
        String deleteDbColumnQuery = "DELETE FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_column_id = ?";

        when(jdbcTemplate.queryForObject(findTableQuery, String.class, "db1", "table1")).thenReturn("1");
        when(jdbcTemplate.queryForObject(findColumnQuery, String.class, "1", "column1")).thenReturn("1");
        when(jdbcTemplate.update(deleteFileColumnQuery, "1", "fileColumn1")).thenReturn(1);
        when(jdbcTemplate.queryForObject(checkFileColumnsQuery, Integer.class, "1")).thenReturn(0); // No remaining file columns
        when(jdbcTemplate.update(deleteDbColumnQuery, "1")).thenReturn(0); // Simulate failure to delete db column

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            dao.deleteFileColumn(table);
        });

        assertEquals("Failed to delete db column: column1", exception.getMessage());
    }
}
