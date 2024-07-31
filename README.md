package com.example.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DirectoryMonitorServiceTest {

    @Mock
    private FileProcessingService fileProcessingService;

    @Mock
    private WatchService watchService;

    @Mock
    private WatchKey watchKey;

    @InjectMocks
    private DirectoryMonitorService directoryMonitorService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        Path mockPath = mock(Path.class);
        FileSystems.getDefault().newWatchService();
        when(mockPath.register(any(WatchService.class), eq(StandardWatchEventKinds.ENTRY_CREATE))).thenReturn(watchKey);
        Field directoryField = DirectoryMonitorService.class.getDeclaredField("directory");
        directoryField.setAccessible(true);
        directoryField.set(directoryMonitorService, mockPath);
    }

    @Test
    public void testWatchDirectory() throws Exception {
        WatchEvent<Path> mockEvent = mock(WatchEvent.class);
        Path mockFilePath = mock(Path.class);
        when(watchKey.pollEvents()).thenReturn(List.of(mockEvent));
        when(mockEvent.context()).thenReturn(mockFilePath);
        when(watchService.take()).thenReturn(watchKey).thenReturn(null);
        when(mockFilePath.toString()).thenReturn("dummyFile.txt");

        doNothing().when(fileProcessingService).processFile(mockFilePath);

        directoryMonitorService.watchDirectory();

        ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        verify(fileProcessingService, times(1)).processFile(pathCaptor.capture());
        assertEquals(mockFilePath, pathCaptor.getValue());
    }

    @Test
    public void testWatchDirectoryWithException() throws Exception {
        when(watchService.take()).thenThrow(new InterruptedException("Mock Exception"));

        directoryMonitorService.watchDirectory();

        verify(fileProcessingService, never()).processFile(any(Path.class));
    }
}



java.lang.NullPointerException: Cannot invoke "com.example.demo.service.DirectoryMonitorService.watchDirectory()" because "this.directoryMonitorService" is null

	at com.example.demo.DirectoryMonitorServiceTest.testWatchDirectoryWithException(DirectoryMonitorServiceTest.java:71)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
