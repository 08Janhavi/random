package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.util.logging.Logger;

@Service
public class DirectoryMonitorService {

    private final Path directory;
    private final FileProcessingService fileProcessingService;
    private static final Logger logger = Logger.getLogger(DirectoryMonitorService.class.getName());

    @Autowired
    public DirectoryMonitorService(FileProcessingService fileProcessingService) {
        this.directory = Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files");  // Directory to monitor
        this.fileProcessingService = fileProcessingService;
    }

    public void watchDirectory() {
        logger.info("Starting directory monitoring for directory: " + directory.toAbsolutePath());

            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
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

import com.example.demo.service.FileProcessingService;
import com.example.demo.service.DirectoryMonitorService;
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

    private DirectoryMonitorService directoryMonitorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        directoryMonitorService = new DirectoryMonitorService(fileProcessorService);
    }

    @Test void testWatchDirectory_whenDirectoryHasFile_creationEventIsProcessed() throws IOException, InterruptedException {
        Path mockPath = Paths.get("dummy/path");
        WatchService mockWatchService = mock(WatchService.class);
        WatchKey mockWatchKey = mock(WatchKey.class);

        when(FileSystems.getDefault().newWatchService()).thenReturn(mockWatchService);
        when(mockPath.register(any(WatchService.class), eq(StandardWatchEventKinds.ENTRY_CREATE))).thenReturn(mockWatchKey);
        when(mockWatchService.take()).thenReturn(mockWatchKey);

        WatchEvent<Path> mockEvent = mock(WatchEvent.class);
        when(mockEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(mockEvent.context()).thenReturn(Paths.get("newfile.txt"));
        when(mockWatchKey.pollEvents()).thenReturn(List.of(mockEvent));

        doNothing().when(fileProcessorService).processFile(any(Path.class));
        when(mockWatchKey.reset()).thenReturn(true);

        directoryMonitorService.watchDirectory();

        verify(fileProcessorService, times(1)).processFile(Paths.get("C:\\Users\\suraj\\Downloads\\testing\\src\\main\\java\\com\\example\\batch\\files\\newfile.txt"));
    }

    @Test
    void testWatchDirectory_whenInterrupted_exceptionIsHandled() throws IOException, InterruptedException {
        Path mockPath = Paths.get("dummy/path");
        WatchService mockWatchService = mock(WatchService.class);

        when(FileSystems.getDefault().newWatchService()).thenReturn(mockWatchService);
        when(mockPath.register(any(WatchService.class), eq(StandardWatchEventKinds.ENTRY_CREATE))).thenThrow(IOException.class);

        directoryMonitorService.watchDirectory();

        // Ensure it ends up in the catch block
        verify(fileProcessorService, never()).processFile(any(Path.class));
    }

    @Test void testWatchDirectory_whenTakeThrowsInterruptedException_itRecoversGracefully() throws InterruptedException, IOException {
        WatchService mockedWatchService = mock(WatchService.class);
        when(FileSystems.getDefault().newWatchService()).thenReturn(mockedWatchService);
        when(mockedWatchService.take()).thenThrow(InterruptedException.class);

        directoryMonitorService.watchDirectory();

        verify(fileProcessorService, never()).processFile(any(Path.class));
    }
}

org.mockito.exceptions.misusing.MissingMethodInvocationException: 
when() requires an argument which has to be 'a method call on a mock'.
For example:
    when(mock.getArticles()).thenReturn(articles);

Also, this error might show up because:
1. you stub either of: final/private/native/equals()/hashCode() methods.
   Those methods *cannot* be stubbed/verified.
   Mocking methods declared on non-public parent classes is not supported.
2. inside when() you don't call method on mock but on some other object.


	at com.example.demo.DirectoryMonitorServiceTests.testWatchDirectory_whenDirectoryHasFile_creationEventIsProcessed(DirectoryMonitorServiceTest.java:32)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)


org.mockito.exceptions.misusing.MissingMethodInvocationException: 
when() requires an argument which has to be 'a method call on a mock'.
For example:
    when(mock.getArticles()).thenReturn(articles);

Also, this error might show up because:
1. you stub either of: final/private/native/equals()/hashCode() methods.
   Those methods *cannot* be stubbed/verified.
   Mocking methods declared on non-public parent classes is not supported.
2. inside when() you don't call method on mock but on some other object.


	at com.example.demo.DirectoryMonitorServiceTests.testWatchDirectory_whenInterrupted_exceptionIsHandled(DirectoryMonitorServiceTest.java:54)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)


org.mockito.exceptions.misusing.MissingMethodInvocationException: 
when() requires an argument which has to be 'a method call on a mock'.
For example:
    when(mock.getArticles()).thenReturn(articles);

Also, this error might show up because:
1. you stub either of: final/private/native/equals()/hashCode() methods.
   Those methods *cannot* be stubbed/verified.
   Mocking methods declared on non-public parent classes is not supported.
2. inside when() you don't call method on mock but on some other object.


	at com.example.demo.DirectoryMonitorServiceTests.testWatchDirectory_whenTakeThrowsInterruptedException_itRecoversGracefully(DirectoryMonitorServiceTest.java:65)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)


Process finished with exit code -1
