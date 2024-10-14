import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;
import java.util.List;

class GenerateServiceTest {

    @Mock
    private IMLineageDataDAO imLineageDao;

    @Mock
    private Logger logger;

    @InjectMocks
    private GenerateService generateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logger = LogManager.getLogger(GenerateService.class); // Ensures logger setup
    }

    @Test
    void generateLineageData_ShouldCallDaoAndLogInfo() {
        // Arrange
        String databaseName = "db1";
        String tableName = "table1";
        List<Table> mockLineageData = Arrays.asList(new Table("db1", "table1", Arrays.asList()));

        when(imLineageDao.getLineageDataFromDB(databaseName, tableName)).thenReturn(mockLineageData);

        // Act
        generateService.generateLineageData(databaseName, tableName);

        // Assert
        verify(imLineageDao, times(1)).getLineageDataFromDB(databaseName, tableName);
        verify(logger, times(1)).info("Starting to generate and publish lineage data");
    }

    @Test
    void generateLineageData_WhenNoLineageData_ShouldStillLog() {
        // Arrange
        String databaseName = "db2";
        String tableName = "table2";
        when(imLineageDao.getLineageDataFromDB(databaseName, tableName)).thenReturn(null);

        // Act
        generateService.generateLineageData(databaseName, tableName);

        // Assert
        verify(imLineageDao, times(1)).getLineageDataFromDB(databaseName, tableName);
        verify(logger, times(1)).info("Starting to generate and publish lineage data");
    }
}
