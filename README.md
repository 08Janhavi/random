import static org.mockito.Mockito.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
public class DirectoryMonitorServiceTest {

    @Mock
    private FileProcessingService mockFileProcessingService;

    @Mock
    private WatchService mockWatchService;

    @InjectMocks
    private DirectoryMonitorService directoryMonitorService;

    private Path tempDirectory;
    private WatchKey mockWatchKey;
    private int counter;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        counter = 0;

        // Create a temporary directory for testing
        tempDirectory = Files.createTempDirectory("testDir");
        mockWatchKey = mock(WatchKey.class);

        // Use ReflectionTestUtils to set the private final field
        ReflectionTestUtils.setField(directoryMonitorService, "directory", tempDirectory);
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Clean up the temporary directory
        Files.walk(tempDirectory)
                .sorted((p1, p2) -> p2.compareTo(p1)) // Delete files before directories
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Test
    public void testWatchDirectory() throws Exception {
        Path mockAbsolutePath = mock(Path.class);
        when(tempDirectory.toAbsolutePath()).thenReturn(mockAbsolutePath);
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
        when(tempDirectory.register(any(WatchService.class), eq(StandardWatchEventKinds.ENTRY_CREATE)))
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
        verify(tempDirectory).toAbsolutePath();
        verify(tempDirectory).register(any(WatchService.class), eq(StandardWatchEventKinds.ENTRY_CREATE));
        verify(mockFileProcessingService, times(2)).processFile(mockFilePath); // Processed twice
    }
}
