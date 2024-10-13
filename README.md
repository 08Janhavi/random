import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(LineageDataController.class)
public class LineageDataControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IMLineageDataDAO lineageDataDAO;

    @InjectMocks
    private LineageDataController lineageDataController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(lineageDataController).build();
    }

    @Test
    public void testGetDatabasesSuccess() throws Exception {
        List<String> databases = new ArrayList<>();
        databases.add("db1");
        databases.add("db2");

        when(lineageDataDAO.getAllDatabases()).thenReturn(databases);

        mockMvc.perform(get("/getDatabases"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value("db1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").value("db2"));
    }

    @Test
    public void testGetDatabasesError() throws Exception {
        when(lineageDataDAO.getAllDatabases()).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(get("/getDatabases"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    public void testGetTablesSuccess() throws Exception {
        List<String> tables = new ArrayList<>();
        tables.add("table1");
        tables.add("table2");

        when(lineageDataDAO.getAllTables()).thenReturn(tables);

        mockMvc.perform(get("/getTables"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value("table1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").value("table2"));
    }

    @Test
    public void testGetTablesError() throws Exception {
        when(lineageDataDAO.getAllTables()).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(get("/getTables"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    public void testGetColumnMappingsSuccess() throws Exception {
        List<Table> tables = new ArrayList<>();
        Table table = new Table("db1", "table1", new ArrayList<>());
        tables.add(table);

        when(lineageDataDAO.getLineageDataFromDB("db1", "table1")).thenReturn(tables);

        mockMvc.perform(get("/getColumnMappings")
                        .param("db", "db1")
                        .param("table", "table1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].databaseName").value("db1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].tableName").value("table1"));
    }

    @Test
    public void testGetColumnMappingsInvalidParams() throws Exception {
        mockMvc.perform(get("/getColumnMappings")
                        .param("db", "")
                        .param("table", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    public void testSaveColumnMappingsSuccess() throws Exception {
        Table table = new Table("db1", "table1", new ArrayList<>());

        mockMvc.perform(post("/saveColumnMappings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(table)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Data saved successfully"));
    }

    @Test
    public void testSaveColumnMappingsInvalidData() throws Exception {
        mockMvc.perform(post("/saveColumnMappings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid data"));
    }

    @Test
    public void testDeleteFileColumnSuccess() throws Exception {
        Table table = new Table("db1", "table1", new ArrayList<>());

        mockMvc.perform(delete("/deleteFileColumn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(table)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("File column deleted successfully"));
    }

    @Test
    public void testDeleteFileColumnInvalidData() throws Exception {
        mockMvc.perform(delete("/deleteFileColumn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid data"));
    }
}
