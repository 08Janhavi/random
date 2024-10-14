import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LineageDataServiceTest {

    private LineageDataService lineageDataService;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Captor
    private ArgumentCaptor<String> queryCaptor;

    @Captor
    private ArgumentCaptor<Object[]> argsCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lineageDataService = new LineageDataService(jdbcTemplate);
    }

    @Test
    void testSaveLineageData_NewTable() {
        Table table = new Table("database1", "table1", Arrays.asList(
                new TableColumn("column1", "process1", Arrays.asList(
                        new FileColumn("file_column1", "file1.csv", "source1")
                ))
        ));

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), any(), any()))
                .thenReturn(null)
                .thenReturn("1");

        lineageDataService.saveLineageData(table);

        verify(jdbcTemplate, times(2)).update(queryCaptor.capture(), argsCaptor.capture());

        List<String> queries = queryCaptor.getAllValues();
        assertEquals("INSERT INTO rawdata.dbo.lineage_data_db_tables (database_name, db_table_name,created_by) VALUES (?, ?,?)", queries.get(0));
        assertEquals("INSERT INTO rawdata.dbo.lineage_data_db_table_columns (db_table_id, db_column_name,process_name,created_by) VALUES (?, ?,?,?)", queries.get(1));

        List<Object[]> args = argsCaptor.getAllValues();
        assertEquals(Arrays.asList("database1", "table1", "CREATED_BY"), Arrays.asList(args.get(0)));
        assertEquals(Arrays.asList("1", "column1", "process1", "CREATED_BY"), Arrays.asList(args.get(1)));
    }

    @Test
    void testSaveLineageData_ExistingTable() {
        Table table = new Table("database1", "table1", Arrays.asList(
                new TableColumn("column1", "process1", Arrays.asList(
                        new FileColumn("file_column1", "file1.csv", "source1")
                ))
        ));

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), any(), any()))
                .thenReturn("1")
                .thenReturn("2");

        lineageDataService.saveLineageData(table);

        verify(jdbcTemplate, times(2)).update(queryCaptor.capture(), argsCaptor.capture());

        List<String> queries = queryCaptor.getAllValues();
        assertEquals("INSERT INTO rawdata.dbo.lineage_data_db_table_columns (db_table_id, db_column_name,process_name,created_by) VALUES (?, ?,?,?)", queries.get(0));
        assertEquals("INSERT INTO rawdata.dbo.lineage_data_file_columns (db_column_id, file_column_name, file_name, file_source,created_by) VALUES (?, ?, ?, ?,?)", queries.get(1));

        List<Object[]> args = argsCaptor.getAllValues();
        assertEquals(Arrays.asList("1", "column1", "process1", "CREATED_BY"), Arrays.asList(args.get(0)));
        assertEquals(Arrays.asList("2", "file_column1", "file1.csv", "source1", "CREATED_BY"), Arrays.asList(args.get(1))); 
    }




@Test
void testSaveLineageData_UpdateColumn() {
    Table table = new Table("database1", "table1", Arrays.asList(
            new TableColumn("column1_updated", "process1", Arrays.asList(
                    new FileColumn("file_column1", "file1.csv", "source1")
            ))
    ));

    when(jdbcTemplate.queryForObject(anyString(), eq(String.class), any(), any()))
            .thenReturn("1")
            .thenReturn("2");

    lineageDataService.saveLineageData(table);

    verify(jdbcTemplate, times(2)).update(queryCaptor.capture(), argsCaptor.capture());

    List<String> queries = queryCaptor.getAllValues();
    assertEquals("UPDATE rawdata.dbo.lineage_data_db_table_columns SET db_column_name = ? WHERE db_column_id = ?", queries.get(0));
    assertEquals("INSERT INTO rawdata.dbo.lineage_data_file_columns (db_column_id, file_column_name, file_name, file_source,created_by) VALUES (?, ?, ?, ?,?)", queries.get(1));

    List<Object[]> args = argsCaptor.getAllValues();
    assertEquals(Arrays.asList("column1_updated", "2"), Arrays.asList(args.get(0)));
    assertEquals(Arrays.asList("2", "file_column1", "file1.csv", "source1", "CREATED_BY"), Arrays.asList(args.get(1)));
}

@Test
void testSaveLineageData_UpdateFileColumn() {
    Table table = new Table("database1", "table1", Arrays.asList(
            new TableColumn("column1", "process1", Arrays.asList(
                    new FileColumn("file_column1_updated", "file2.csv", "source2")
            ))
    ));

    when(jdbcTemplate.queryForObject(anyString(), eq(String.class), any(), any()))
            .thenReturn("1")
            .thenReturn("2")
            .thenReturn("3");

    lineageDataService.saveLineageData(table);

    verify(jdbcTemplate, times(2)).update(queryCaptor.capture(), argsCaptor.capture());

    List<String> queries = queryCaptor.getAllValues();
    assertEquals("UPDATE rawdata.dbo.lineage_data_file_columns SET file_column_name=?,file_name = ?, file_source = ? WHERE file_column_id = ?", queries.get(1));

    List<Object[]> args = argsCaptor.getAllValues();
    assertEquals(Arrays.asList("file_column1_updated", "file2.csv", "source2", "3"), Arrays.asList(args.get(1)));
}




