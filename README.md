import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.*;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
public class DirectoryMonitorServiceTest {

    @Mock
    private FileProcessingService mockFileProcessingService;

    @Mock
    private WatchService mockWatchService;

    @Mock
    private Path mockDirectoryPath;

    @Mock
    private WatchKey mockWatchKey;

    private DirectoryMonitorService directoryMonitorService;
    private int counter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        counter = 0;

        // Instantiate DirectoryMonitorService with the mocked WatchService
        directoryMonitorService = new DirectoryMonitorService(mockFileProcessingService, mockWatchService);

        // Use ReflectionTestUtils to set the private final field
        ReflectionTestUtils.setField(directoryMonitorService, "directory", mockDirectoryPath);
    }

    @Test
    public void testWatchDirectory() throws Exception {
        Path mockAbsolutePath = mock(Path.class);
        when(mockDirectoryPath.toAbsolutePath()).thenReturn(mockAbsolutePath);
        when(mockAbsolutePath.toString()).thenReturn("mockAbsolutePath");

        // Mock WatchService and WatchKey behavior
        when(mockWatchService.take()).thenAnswer(invocation -> {
            if (counter++ < 2) return mockWatchKey; // Return mockWatchKey for the first 2 calls
            else return null; // Simulate no more events to stop the loop
        });

        WatchEvent<Path> mockWatchEvent = mock(WatchEvent.class);
        Path mockFilePath = mock(Path.class);
        when(mockWatchEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(mockWatchEvent.context()).thenReturn(mockFilePath);
        when(mockWatchKey.pollEvents()).thenReturn(Collections.singletonList(mockWatchEvent));

        // Mock the directory registration
        when(mockDirectoryPath.register(any(WatchService.class), eq(StandardWatchEventKinds.ENTRY_CREATE)))
                .thenReturn(mockWatchKey);

        // Run the method under test in a separate thread
        Thread thread = new Thread(() -> {
            try {
                directoryMonitorService.watchDirectory();
            } catch (Exception e) {
                // Handle or log exception if necessary
                e.printStackTrace();
            }
        });
        thread.start();

        // Wait for a bit to let the thread run
        TimeUnit.SECONDS.sleep(1);

        // Interrupt the thread to stop execution
        thread.interrupt();
        thread.join();

        // Verify interactions with the mocks
        verify(mockDirectoryPath).toAbsolutePath();
        verify(mockDirectoryPath).register(any(WatchService.class), eq(StandardWatchEventKinds.ENTRY_CREATE));
        verify(mockFileProcessingService, times(2)).processFile(mockFilePath); // Processed twice
    }
}
