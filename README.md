package com.nomura.im.lineage.dao;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.nomura.im.lineage.vo.Table;
import com.nomura.im.lineage.vo.TableColumn;
import com.nomura.im.lineage.vo.FileColumn;

public class IMLineageDataDAOTest {

    @InjectMocks
    private IMLineageDataDAO imLineageDataDAO;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetLineageDataFromDB() throws SQLException {
        // Mocking ResultSet for table
        when(resultSet.getString("db_table_id")).thenReturn("tableId1");
        when(resultSet.getString("database_name")).thenReturn("database1");
        when(resultSet.getString("db_table_name")).thenReturn("table1");

        // Mocking ResultSet for table column
        when(resultSet.getString("db_column_id")).thenReturn("columnId1");
        when(resultSet.getString("db_column_name")).thenReturn("columnName1");
        when(resultSet.getString("process_name")).thenReturn("process1");

        // Mocking ResultSet for file column
        when(resultSet.getString("file_column_name")).thenReturn("fileColumnName1");
        when(resultSet.getString("file_name")).thenReturn("fileName1");
        when(resultSet.getString("file_source")).thenReturn("fileSource1");

        // Define RowMappers for each entity
        RowMapper<Table> tableRowMapper = (rs, rowNum) -> new Table(
                rs.getString("db_table_id"),
                rs.getString("database_name"),
                rs.getString("db_table_name"),
                new ArrayList<>()
        );

        RowMapper<TableColumn> tableColumnRowMapper = (rs, rowNum) -> new TableColumn(
                rs.getString("db_column_id"),
                rs.getString("db_column_name"),
                rs.getString("process_name"),
                new ArrayList<>()
        );

        RowMapper<FileColumn> fileColumnRowMapper = (rs, rowNum) -> new FileColumn(
                rs.getString("file_column_name"),
                rs.getString("file_name"),
                rs.getString("file_source")
        );

        // Mocking JdbcTemplate queries with appropriate RowMappers
        when(jdbcTemplate.query(eq("SELECT db_table_id,database_name,db_table_name FROM dbo.lineage_data_db_tables"), any(RowMapper.class)))
                .thenReturn(Arrays.asList(tableRowMapper.mapRow(resultSet, 1)));

        when(jdbcTemplate.query(eq("SELECT db_column_id,db_column_name,process_name FROM dbo.lineage_data_db_table_columns WHERE db_table_id=?"), eq(new Object[]{"tableId1"}), any(RowMapper.class)))
                .thenReturn(Arrays.asList(tableColumnRowMapper.mapRow(resultSet, 1)));

        when(jdbcTemplate.query(eq("SELECT file_column_name,file_name,file_source FROM dbo.lineage_data_file_columns WHERE db_column_id=?"), eq(new Object[]{"columnId1"}), any(RowMapper.class)))
                .thenReturn(Arrays.asList(fileColumnRowMapper.mapRow(resultSet, 1)));

        // Run the method to test
        List<Table> lineageData = imLineageDataDAO.getLineageDataFromDB();

        // Verify the results
        assertNotNull(lineageData);
        assertEquals(1, lineageData.size());
        Table resultTable = lineageData.get(0);
        assertEquals("tableId1", resultTable.tableId());
        assertEquals("database1", resultTable.databaseName());
        assertEquals("table1", resultTable.tableName());

        List<TableColumn> resultColumns = resultTable.tableColumns();
        assertEquals(1, resultColumns.size());
        TableColumn resultColumn = resultColumns.get(0);
        assertEquals("columnId1", resultColumn.columnId());
        assertEquals("columnName1", resultColumn.columnName());
        assertEquals("process1", resultColumn.processName());

        List<FileColumn> resultFileColumns = resultColumn.fileColumns();
        assertEquals(1, resultFileColumns.size());
        FileColumn resultFileColumn = resultFileColumns.get(0);
        assertEquals("fileColumnName1", resultFileColumn.columnName());
        assertEquals("fileName1", resultFileColumn.fileName());
        assertEquals("fileSource1", resultFileColumn.fileSource());

        // Verify that queries were called with the correct parameters
        verify(jdbcTemplate, times(1)).query(eq("SELECT db_table_id,database_name,db_table_name FROM dbo.lineage_data_db_tables"), any(RowMapper.class));
        verify(jdbcTemplate, times(1)).query(eq("SELECT db_column_id,db_column_name,process_name FROM dbo.lineage_data_db_table_columns WHERE db_table_id=?"), eq(new Object[]{"tableId1"}), any(RowMapper.class));
        verify(jdbcTemplate, times(1)).query(eq("SELECT file_column_name,file_name,file_source FROM dbo.lineage_data_file_columns WHERE db_column_id=?"), eq(new Object[]{"columnId1"}), any(RowMapper.class));
    }

    @Test
    public void testGetLineageDataFromDB_NoTables() {
        // Mock empty dbTable data
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(Collections.emptyList());

        // Run the method to test
        List<Table> lineageData = imLineageDataDAO.getLineageDataFromDB();

        // Verify the results
        assertNotNull(lineageData);
        assertTrue(lineageData.isEmpty());

        // Verify that the query was called
        verify(jdbcTemplate, times(1)).query(eq("SELECT db_table_id,database_name,db_table_name FROM dbo.lineage_data_db_tables"), any(RowMapper.class));
    }

    @Test
    public void testGetLineageDataFromDB_TableWithoutColumns() {
        // Mock dbTable data
        Table tableMock = new Table("tableId1", "database1", "table1", Collections.emptyList());
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(Arrays.asList(tableMock));

        // Mock empty tableColumn data
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RowMapper.class)))
                .thenReturn(Collections.emptyList());

        // Run the method to test
        List<Table> lineageData = imLineageDataDAO.getLineageDataFromDB();

        // Verify the results
        assertNotNull(lineageData);
        assertEquals(1, lineageData.size());
        Table resultTable = lineageData.get(0);
        assertEquals("tableId1", resultTable.tableId());
        assertTrue(resultTable.tableColumns().isEmpty());

        // Verify that the queries were called
        verify(jdbcTemplate, times(1)).query(eq("SELECT db_table_id,database_name,db_table_name FROM dbo.lineage_data_db_tables"), any(RowMapper.class));
        verify(jdbcTemplate, times(1)).query(eq("SELECT db_column_id,db_column_name,process_name FROM dbo.lineage_data_db_table_columns WHERE db_table_id=?"), eq(new Object[]{"tableId1"}), any(RowMapper.class));
    }

    @Test
    public void testGetLineageDataFromDB_ColumnWithoutFileColumns() {
        // Mock dbTable data
        TableColumn columnMock = new TableColumn("columnId1", "columnName1", "process1", new ArrayList<>());
        when(jdbcTemplate.query(eq("SELECT db_column_id,db_column_name,process_name FROM dbo.lineage_data_db_table_columns WHERE db_table_id=?"), eq(new Object[]{"tableId1"}), any(RowMapper.class)))
                .thenReturn(Arrays.asList(columnMock));

        Table tableMock = new Table("tableId1", "database1", "table1", new ArrayList<>());
        when(jdbcTemplate.query(eq("SELECT db_table_id,database_name,db_table_name FROM dbo.lineage_data_db_tables"), any(RowMapper.class)))
                .thenReturn(Arrays.asList(tableMock));

        // Mock empty fileColumn data
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RowMapper.class)))
                .thenReturn(Collections.emptyList());

        // Run the method to test
        List<Table> lineageData = imLineageDataDAO.getLineageDataFromDB();

        // Verify the results
        assertNotNull(lineageData);
        assertEquals(1, lineageData.size());
        Table resultTable = lineageData.get(0);
        assertTrue(resultTable.tableColumns().isEmpty());

        // Verify that the queries were called
        verify(jdbcTemplate, times(1)).query(eq("SELECT db_table_id,database_name,db_table_name FROM dbo.lineage_data_db_tables"), any(RowMapper.class));
        verify(jdbcTemplate, times(1)).query(eq("SELECT db_column_id,db_column_name,process_name FROM dbo.lineage_data_db_table_columns WHERE db_table_id=?"), eq(new Object[]{"tableId1"}), any(RowMapper.class));
    }
}
