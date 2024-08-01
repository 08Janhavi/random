package com.example.demo;

import com.example.demo.service.DirectoryMonitorService;
import com.example.demo.service.FileProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.*;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

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
    public void setUp() throws Exception {
        testDirectory = Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files");
        directoryMonitorService = new DirectoryMonitorService(fileProcessingService, watchService);
        // Set the private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(directoryMonitorService, "directory", testDirectory);
        ReflectionTestUtils.setField(directoryMonitorService, "watchService", watchService);

        // Mock the WatchService and WatchKey behavior
        when(watchKey.pollEvents()).thenReturn(Collections.singletonList(mock(WatchEvent.class)));
        when(watchService.take()).thenReturn(watchKey);
    }

    @Test
    public void testWatchDirectory() throws Exception {
        // Arrange
        WatchEvent<Path> mockEvent = mock(WatchEvent.class);
        Path mockPath = Paths.get("testFile.txt");
        when(mockEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(mockEvent.context()).thenReturn(mockPath);

        // Ensure the WatchKey returns the mock event
        when(watchKey.pollEvents()).thenReturn(Collections.singletonList(mockEvent));

        // Mock the FileProcessingService to do nothing
        doNothing().when(fileProcessingService).processFile(any(Path.class));

        CountDownLatch latch = new CountDownLatch(1);

        // Act
        Thread monitorThread = new Thread(() -> {
            directoryMonitorService.watchDirectory();
            latch.countDown();
        });
        monitorThread.start();

        // Give the monitor thread a moment to start and process
        Thread.sleep(500);

        // Simulate the WatchService behavior to trigger the WatchKey
        verify(watchService, times(1)).take();  // Verify the watch service was polled

        // Wait for the latch to ensure the thread has finished processing
        latch.await();

        // Stop the directory monitoring
        monitorThread.interrupt();
        monitorThread.join(1000);

        // Assert
        verify(fileProcessingService, times(1)).processFile(testDirectory.resolve(mockPath));
    }
}
