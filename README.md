package com.example.demo;

import com.example.demo.service.DirectoryMonitorService;
import com.example.demo.service.FileProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

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

        CountDownLatch latch = new CountDownLatch(1);

        // Act
        Thread monitorThread = new Thread(() -> {
            directoryMonitorService.watchDirectory();
            latch.countDown();
        });
        monitorThread.start();

        // Give the monitor thread a moment to start
        Thread.sleep(500);

        // Simulate a file creation event
        watchService.take();  // This should trigger the processing

        // Wait for the latch to count down
        latch.await();

        // Interrupt the monitor thread to stop the infinite loop
        monitorThread.interrupt();
        monitorThread.join();

        // Assert
        verify(fileProcessingService, times(1)).processFile(testDirectory.resolve(mockPath));
    }
}
