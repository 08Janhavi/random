package com.example.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectoryMonitorServiceTest {

    @Mock
    private FileProcessingService fileProcessingService;

    @Mock
    private WatchService watchService;

    @InjectMocks
    private DirectoryMonitorService directoryMonitorService;

    private Path directory;
    private WatchKey basePathWatchKey;

    @BeforeEach
    void setUp() throws Exception {
        directory = Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files");
        basePathWatchKey = directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Test
    void testEventForDirectory() throws Exception {
        // Mock WatchKey and WatchEvent
        WatchKey mockWatchKey = mock(WatchKey.class);
        WatchEvent<Path> mockWatchEvent1 = mock(WatchEvent.class);
        WatchEvent<Path> mockWatchEvent2 = mock(WatchEvent.class);
        WatchEvent<Path> mockWatchEvent3 = mock(WatchEvent.class);

        // Mock behavior of pollEvents and watchKey
        when(mockWatchEvent1.context()).thenReturn(Paths.get("newTextFile.txt"));
        when(mockWatchEvent1.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(mockWatchEvent1.count()).thenReturn(1);

        when(mockWatchEvent2.context()).thenReturn(Paths.get("newTextFileII.txt"));
        when(mockWatchEvent2.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(mockWatchEvent2.count()).thenReturn(1);

        when(mockWatchEvent3.context()).thenReturn(Paths.get("newTextFileIII.txt"));
        when(mockWatchEvent3.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(mockWatchEvent3.count()).thenReturn(1);

        when(mockWatchKey.pollEvents()).thenReturn(List.of(mockWatchEvent1, mockWatchEvent2, mockWatchEvent3));
        when(mockWatchKey.reset()).thenReturn(true);
        when(watchService.poll(20, TimeUnit.SECONDS)).thenReturn(mockWatchKey);

        // Run the watchDirectory method in a separate thread
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                directoryMonitorService.watchDirectory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Allow some time for the monitoring process to detect the event
        TimeUnit.SECONDS.sleep(1);

        // Verify that the fileProcessingService's processFile method was called with the correct path
        ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        verify(fileProcessingService, times(3)).processFile(pathCaptor.capture());

        List<Path> capturedPaths = pathCaptor.getAllValues();
        assertThat(capturedPaths.get(0), is(directory.resolve("newTextFile.txt")));
        assertThat(capturedPaths.get(1), is(directory.resolve("newTextFileII.txt")));
        assertThat(capturedPaths.get(2), is(directory.resolve("newTextFileIII.txt")));

        // Verify that the watchKey was reset
        verify(mockWatchKey, times(1)).reset();
    }

    @Test
    void testEventForSubDirectory() throws Exception {
        Path subDirectory = directory.resolve("subDir");
        subDirectory.toFile().mkdirs(); // Create the sub-directory
        WatchKey subDirWatchKey = subDirectory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        // Mock WatchKey and WatchEvent for sub-directory
        WatchKey mockWatchKey = mock(WatchKey.class);
        WatchEvent<Path> mockWatchEvent = mock(WatchEvent.class);

        // Mock behavior of pollEvents and watchKey
        when(mockWatchEvent.context()).thenReturn(Paths.get("newTextFile.txt"));
        when(mockWatchEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(mockWatchEvent.count()).thenReturn(1);

        when(mockWatchKey.pollEvents()).thenReturn(List.of(mockWatchEvent));
        when(mockWatchKey.reset()).thenReturn(true);
        when(watchService.poll(20, TimeUnit.SECONDS)).thenReturn(mockWatchKey);

        // Generate a file in the sub-directory
        Files.createFile(subDirectory.resolve("newTextFile.txt"));

        // Run the watchDirectory method in a separate thread
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                directoryMonitorService.watchDirectory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Allow some time for the monitoring process to detect the event
        TimeUnit.SECONDS.sleep(1);

        // Verify that the fileProcessingService's processFile method was called with the correct path
        ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        verify(fileProcessingService).processFile(pathCaptor.capture());
        assertThat(pathCaptor.getValue(), is(subDirectory.resolve("newTextFile.txt")));

        // Verify that the watchKey was reset
        verify(mockWatchKey, times(1)).reset();
    }
}
