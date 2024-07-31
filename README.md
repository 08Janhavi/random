package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.util.logging.Logger;

@Service
public class DirectoryMonitorService {

    private final Path directory;
    private final FileProcessingService fileProcessingService;
    private final WatchService watchService;
    private static final Logger logger = Logger.getLogger(DirectoryMonitorService.class.getName());

    @Autowired
    public DirectoryMonitorService(FileProcessingService fileProcessingService, WatchService watchService, Path directory) {
        this.fileProcessingService = fileProcessingService;
        this.watchService = watchService;
        this.directory = directory;
    }

    public void watchDirectory() {
        logger.info("Starting directory monitoring for directory: " + directory.toAbsolutePath());

        try {
            directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path filePath = directory.resolve((Path) event.context());
                    logger.info("Detected new file: " + filePath.toString());
                    fileProcessingService.processFile(filePath);
                }
                key.reset();
            }
        } catch (Exception e) {
            logger.severe("Error in directory monitoring: " + e.getMessage());
        }
    }
}


package com.example.demo;

import com.example.demo.service.DirectoryMonitorService;
import com.example.demo.service.FileProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.*;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;

class DirectoryMonitorServiceTests {

    @Mock
    private FileProcessingService fileProcessorService;

    @Mock
    private WatchService watchService;

    @Mock
    private Path directory;

    private DirectoryMonitorService directoryMonitorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        directoryMonitorService = new DirectoryMonitorService(fileProcessorService, watchService, directory);
    }

    @Test
    void testWatchDirectory_whenDirectoryHasFile_creationEventIsProcessed() throws IOException, InterruptedException {
        WatchKey mockWatchKey = mock(WatchKey.class);
        WatchEvent<Path> mockEvent = mock(WatchEvent.class);

        when(directory.register(any(WatchService.class), eq(StandardWatchEventKinds.ENTRY_CREATE))).thenReturn(mockWatchKey);
        when(watchService.take()).thenReturn(mockWatchKey);
        when(mockEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(mockEvent.context()).thenReturn(Paths.get("newfile.txt"));
        when(mockWatchKey.pollEvents()).thenReturn(List.of(mockEvent));
        when(directory.resolve(any(Path.class))).thenReturn(Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files\\newfile.txt"));

        doNothing().when(fileProcessorService).processFile(any(Path.class));
        when(mockWatchKey.reset()).thenReturn(true);

        directoryMonitorService.watchDirectory();

        verify(fileProcessorService, times(1)).processFile(Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files\\newfile.txt"));
    }

    @Test
    void testWatchDirectory_whenInterrupted_exceptionIsHandled() throws IOException, InterruptedException {
        when(directory.register(any(WatchService.class), eq(StandardWatchEventKinds.ENTRY_CREATE))).thenThrow(IOException.class);

        directoryMonitorService.watchDirectory();

        // Ensure it ends up in the catch block
        verify(fileProcessorService, never()).processFile(any(Path.class));
    }

    @Test
    void testWatchDirectory_whenTakeThrowsInterruptedException_itRecoversGracefully() throws InterruptedException, IOException {
        when(watchService.take()).thenThrow(InterruptedException.class);

        directoryMonitorService.watchDirectory();

        verify(fileProcessorService, never()).processFile(any(Path.class));
    }
}
