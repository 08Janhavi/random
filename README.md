package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Logger;

@Service
public class DirectoryMonitorService {

    @Value("${directory.watch.path}")
    private Path directory;
    @Autowired
    private FileProcessingService fileProcessingService;
    private final WatchService watchService;
    private WatchKey key;
    private static final Logger logger = Logger.getLogger(DirectoryMonitorService.class.getName());

    public DirectoryMonitorService() throws IOException {
        this.watchService=FileSystems.getDefault().newWatchService();;
    }

//    @Autowired
//    public DirectoryMonitorService(FileProcessingService fileProcessingService) throws IOException {
//        this.directory = Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files");  // Directory to monitor
//        this.fileProcessingService = fileProcessingService;
//        this.watchService=FileSystems.getDefault().newWatchService();
//    }

    public void watchDirectory() {
        logger.info("Starting directory monitoring for directory: " + directory.toAbsolutePath());

            try ( watchService) {
                directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);


                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path filePath = directory.resolve((Path) event.context());
                        logger.info("Detected new file: " + filePath);
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DirectoryMonitorServiceTest {

    @Mock
    private Path directory;

    @Mock
    private FileProcessingService fileProcessingService;

    @Mock
    private WatchService watchService;

    @Mock
    private WatchKey watchKey;

    private DirectoryMonitorService setMockers() throws IOException {
        DirectoryMonitorService directoryMonitorService=new DirectoryMonitorService();
        ReflectionTestUtils.setField(directoryMonitorService,"directory",directory);
        ReflectionTestUtils.setField(directoryMonitorService,"fileProcessingService",fileProcessingService);
        ReflectionTestUtils.setField(directoryMonitorService,"watchService",watchService);
        return directoryMonitorService;
    }

    @Test
    public void testWatchDirectory() throws InterruptedException, IOException {
        DirectoryMonitorService directoryMonitorService=setMockers();
        Path relativePath=Paths.get("testFile.txt");
        WatchEvent<Path> mockEvent=mock(WatchEvent.class);
        when(mockEvent.context()).thenReturn(relativePath);
        when(directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE)).thenReturn(watchKey);
        when(watchService.take()).thenReturn(watchKey);
        when(watchKey.pollEvents()).thenReturn(Collections.singletonList(mockEvent));
        when(watchKey.reset()).thenReturn(true);

        AtomicInteger counter= new AtomicInteger();
        when(watchService.take()).thenAnswer(invocation->{
            if(counter.incrementAndGet() >1){
                return null;
            }
            return watchKey;
        });

        directoryMonitorService.watchDirectory();
        Path expectedPath=directory.resolve(relativePath);
//
        verify(fileProcessingService, times(1)).processFile(expectedPath);
    }

}
