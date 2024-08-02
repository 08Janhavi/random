package com.example.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.*;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

class DirectoryMonitorServiceTest {

    private FileProcessingService fileProcessingService;
    private DirectoryMonitorService directoryMonitorService;
    private WatchService mockWatchService;
    private WatchKey mockWatchKey;

    @BeforeEach
    void setUp() throws Exception {
        fileProcessingService = mock(FileProcessingService.class);
        mockWatchService = mock(WatchService.class);
        mockWatchKey = mock(WatchKey.class);

        // Mock WatchService behavior
        when(mockWatchService.take()).thenReturn(mockWatchKey);

        // Initialize DirectoryMonitorService with mocked WatchService
        directoryMonitorService = new DirectoryMonitorService(fileProcessingService);
        ReflectionTestUtils.setField(directoryMonitorService, "watchService", mockWatchService);

        // Configure the mockWatchKey to return a specific WatchEvent
        WatchEvent<Path> mockEvent = mock(WatchEvent.class);
        Path filePath = Paths.get("testFile.txt");
        when(mockEvent.context()).thenReturn(filePath);
        when(mockWatchKey.pollEvents()).thenReturn(Collections.singletonList(mockEvent));
    }

    @Test
    void testWatchDirectory() throws Exception {
        // Run the method in a separate thread to avoid blocking the test
        Thread thread = new Thread(() -> {
            try {
                directoryMonitorService.watchDirectory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join();

        // Verify interactions
        verify(fileProcessingService).processFile(Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files\\testFile.txt"));
    }

    @Test
    void testWatchDirectoryException() throws Exception {
        // Simulate an exception when taking from the WatchService
        when(mockWatchService.take()).thenThrow(new RuntimeException("WatchService exception"));

        // Run the method in a separate thread to avoid blocking the test
        Thread thread = new Thread(() -> {
            try {
                directoryMonitorService.watchDirectory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join();

        // Verify exception handling (you can enhance this based on your logging or error handling logic)
        // For example, check if the error message was logged correctly
    }
}
