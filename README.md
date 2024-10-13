import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class LineageDataControllerTest {

    @Mock
    private IMLineageDataDAO lineageDataDAO;

    @InjectMocks
    private LineageDataController controller;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGetDatabases() throws Exception {
        List<String> databases = Arrays.asList("db1", "db2");
        when(lineageDataDAO.getAllDatabases()).thenReturn(databases);

        mockMvc.perform(get("/getDatabases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("db1"))
                .andExpect(jsonPath("$[1]").value("db2"));

        verify(lineageDataDAO, times(1)).getAllDatabases();
    }

    @Test
    public void testGetDatabasesWithError() throws Exception {
        when(lineageDataDAO.getAllDatabases()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/getDatabases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(lineageDataDAO, times(1)).getAllDatabases();
    }

    @Test
    public void testGetTables() throws Exception {
        List<String> tables = Arrays.asList("table1", "table2");
        when(lineageDataDAO.getAllTables()).thenReturn(tables);

        mockMvc.perform(get("/getTables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("table1"))
                .andExpect(jsonPath("$[1]").value("table2"));

        verify(lineageDataDAO, times(1)).getAllTables();
    }

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

    @Test
    public void testSaveColumnMappings() throws Exception {
        Table table = new Table("testDb", "testTable", Collections.emptyList());

        mockMvc.perform(post("/saveColumnMappings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dbName\": \"testDb\", \"tableName\": \"testTable\"}")
        ).andExpect(status().isOk());

        verify(lineageDataDAO, times(1)).saveLineageData(any(Table.class));
    }

    @Test
    public void testDeleteFileColumn() throws Exception {
        Table table = new Table("testDb", "testTable", Collections.emptyList());

        mockMvc.perform(delete("/deleteFileColumn")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dbName\": \"testDb\", \"tableName\": \"testTable\", \"tableColumns\": []}")
        ).andExpect(status().isOk());

        verify(lineageDataDAO, times(1)).deleteFileColumn(any(Table.class));
    }

    @Test
    public void testDeleteFileColumnWithError() throws Exception {
        Table table = new Table("testDb", "testTable", Collections.emptyList());
        doThrow(new Exception("Delete error")).when(lineageDataDAO).deleteFileColumn(any(Table.class));

        mockMvc.perform(delete("/deleteFileColumn")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dbName\": \"testDb\", \"tableName\": \"testTable\", \"tableColumns\": []}")
        ).andExpect(status().isInternalServerError());

        verify(lineageDataDAO, times(1)).deleteFileColumn(any(Table.class));
    }

    @Test
    public void testSaveColumnMappingsWithInvalidData() throws Exception {
        mockMvc.perform(post("/saveColumnMappings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        verify(lineageDataDAO, times(0)).saveLineageData(any(Table.class));
    }
}
