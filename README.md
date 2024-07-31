import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.List;

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
        directoryMonitorService = new DirectoryMonitorService(fileProcessingService);
    }

    @Test
    public void testWatchDirectory() throws Exception {
        // Arrange
        WatchEvent<Path> mockEvent = (WatchEvent<Path>) mock(WatchEvent.class);
        Path mockPath = Paths.get("testFile.txt");
        when(mockEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(mockEvent.context()).thenReturn(mockPath);

        List<WatchEvent<?>> mockEvents = Collections.singletonList(mockEvent);
        when(watchKey.pollEvents()).thenReturn(mockEvents);
        when(watchService.take()).thenReturn(watchKey);

        doNothing().when(fileProcessingService).processFile(any(Path.class));

        // Act
        // We run the watchDirectory method in a separate thread to simulate the actual behavior
        Thread monitorThread = new Thread(() -> directoryMonitorService.watchDirectory());
        monitorThread.start();
        
        // Give it a moment to process
        Thread.sleep(1000);
        
        // Interrupt the thread to stop the infinite loop
        monitorThread.interrupt();

        // Assert
        verify(fileProcessingService, times(1)).processFile(testDirectory.resolve(mockPath));
    }
}
