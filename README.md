import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.nomura.im.lineage.dao.IMLineageDataDAO;
import com.nomura.im.lineage.vo.FileColumn;
import com.nomura.im.lineage.vo.Table;
import com.nomura.im.lineage.vo.TableColumn;

public class IMLineageDataDAOTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private IMLineageDataDAO imLineageDataDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetLineageDataFromDB() throws SQLException {
        // Mocking the ResultSet and the data
        ResultSet tableResultSet = mock(ResultSet.class);
        when(tableResultSet.getString("db_table_id")).thenReturn("tableId1");
        when(tableResultSet.getString("database_name")).thenReturn("databaseName1");
        when(tableResultSet.getString("db_table_name")).thenReturn("tableName1");

        ResultSet columnResultSet = mock(ResultSet.class);
        when(columnResultSet.getString("db_column_id")).thenReturn("columnId1");
        when(columnResultSet.getString("db_column_name")).thenReturn("columnName1");
        when(columnResultSet.getString("process_name")).thenReturn("processName1");

        ResultSet fileColumnResultSet = mock(ResultSet.class);
        when(fileColumnResultSet.getString("file_column_name")).thenReturn("fileColumnName1");
        when(fileColumnResultSet.getString("file_name")).thenReturn("fileName1");
        when(fileColumnResultSet.getString("file_source")).thenReturn("fileSource1");

        // Mock the queries
        // Mock dbTables query
        List<Table> mockTables = new ArrayList<>();
        mockTables.add(new Table("tableId1", "databaseName1", "tableName1", new ArrayList<>()));
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenAnswer(invocation -> {
                    RowMapper<Table> rowMapper = invocation.getArgument(1);
                    List<Table> tables = new ArrayList<>();
                    tables.add(rowMapper.mapRow(tableResultSet, 0));
                    return tables;
                });

        // Mock tableColumns query
        List<TableColumn> mockColumns = new ArrayList<>();
        mockColumns.add(new TableColumn("columnId1", "columnName1", "processName1", new ArrayList<>()));
        when(jdbcTemplate.query(eq("SELECT db_column_id,db_column_name,process_name FROM dbo.lineage_data_db_table_columns WHERE db_table_id=?"),
                any(Object[].class), any(RowMapper.class)))
                .thenAnswer(invocation -> {
                    RowMapper<TableColumn> rowMapper = invocation.getArgument(2);
                    List<TableColumn> columns = new ArrayList<>();
                    columns.add(rowMapper.mapRow(columnResultSet, 0));
                    return columns;
                });

        // Mock fileColumns query
        List<FileColumn> mockFileColumns = new ArrayList<>();
        mockFileColumns.add(new FileColumn("fileColumnName1", "fileName1", "fileSource1"));
        when(jdbcTemplate.query(eq("SELECT file_column_name,file_name,file_source FROM dbo.lineage_data_file_columns WHERE db_column_id=?"),
                any(Object[].class), any(RowMapper.class)))
                .thenAnswer(invocation -> {
                    RowMapper<FileColumn> rowMapper = invocation.getArgument(2);
                    List<FileColumn> fileColumns = new ArrayList<>();
                    fileColumns.add(rowMapper.mapRow(fileColumnResultSet, 0));
                    return fileColumns;
                });

        // Execute the method
        List<Table> lineageData = imLineageDataDAO.getLineageDataFromDB();

        // Assertions
        assertNotNull(lineageData);
        assertEquals(1, lineageData.size());

        Table table = lineageData.get(0);
        assertEquals("tableId1", table.getDbTableId());
        assertEquals("databaseName1", table.getDatabaseName());
        assertEquals("tableName1", table.getDbTableName());
        assertEquals(1, table.getTableColumns().size());

        TableColumn tableColumn = table.getTableColumns().get(0);
        assertEquals("columnId1", tableColumn.getColumnId());
        assertEquals("columnName1", tableColumn.getColumnName());
        assertEquals("processName1", tableColumn.getProcessName());
        assertEquals(1, tableColumn.getFileColumns().size());

        FileColumn fileColumn = tableColumn.getFileColumns().get(0);
        assertEquals("fileColumnName1", fileColumn.getFileColumnName());
        assertEquals("fileName1", fileColumn.getFileName());
        assertEquals("fileSource1", fileColumn.getFileSource());
    }
}
