import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.*;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DirectoryMonitorServiceTest {

    @Mock
    private FileProcessingService fileProcessingService;

    @Mock
    private WatchService watchService;

    @Mock
    private WatchKey watchKey;

    @InjectMocks
    private DirectoryMonitorService directoryMonitorService;

    private Path testDirectory;

    @BeforeEach
    public void setUp() {
        testDirectory = Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files");
        directoryMonitorService = new DirectoryMonitorService(fileProcessingService, watchService);
    }

    @Test
    public void testWatchDirectory() throws Exception {
        // Arrange
        WatchEvent<Path> mockEvent = (WatchEvent<Path>) mock(WatchEvent.class);
        Path mockPath = Paths.get("testFile.txt");
        when(mockEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(mockEvent.context()).thenReturn(mockPath);

        when(watchKey.pollEvents()).thenReturn(Collections.singletonList(mockEvent));
        when(watchService.take()).thenReturn(watchKey);

        doNothing().when(fileProcessingService).processFile(any(Path.class));

        // Act
        Thread monitorThread = new Thread(() -> directoryMonitorService.watchDirectory());
        monitorThread.start();

        // Simulate a file creation event
        watchService.take();  // This should trigger the processing
        Thread.sleep(1000);

        monitorThread.interrupt();

        // Assert
        verify(fileProcessingService, times(1)).processFile(testDirectory.resolve(mockPath));
    }
}
