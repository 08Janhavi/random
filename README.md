package com.example.demo;


import com.example.demo.service.DirectoryMonitorService;
import com.example.demo.service.FileProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DirectoryMonitorServiceTest {

//    private DirectoryMonitorService setMockers(){
//        DirectoryMonitorService directoryMonitorService=new DirectoryMonitorService();
//        ReflectionTestUtils.setField(directoryMonitorService,"directory","C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files");
//        ReflectionTestUtils.setField(directoryMonitorService,"fileProcessingService","fileProcessingService");
//    }

    @Mock
    private FileProcessingService fileProcessingService;

    @Mock
    private WatchService watchService;

    @Mock
    private WatchKey watchKey;

    @Mock
    private Path testDirectory;

    private DirectoryMonitorService directoryMonitorService;

    @BeforeEach
    public void setup() throws IOException {
//         testDirectory = Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files");
         ReflectionTestUtils.setField(directoryMonitorService,"directory","C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files");
         directoryMonitorService= new DirectoryMonitorService(fileProcessingService,watchKey);
    }

    @Test
    public void testWatchDirectory() throws IOException, InterruptedException {
        when(testDirectory.toAbsolutePath()).thenReturn(testDirectory);
        when(testDirectory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE)).thenReturn(watchKey);
        when(watchService.take()).thenReturn(watchKey);
        when(watchKey.pollEvents()).thenReturn(List.of(mock(WatchEvent.class)));
        Path relativePath=Paths.get("testFile.txt");
        when(mock(WatchEvent.class).context()).thenReturn(relativePath);
        Path expectedPath=Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files\\testFile.txt");
        assertEquals(expectedPath,relativePath);
        when(watchKey.reset()).thenReturn(true);

        directoryMonitorService.watchDirectory();

        verify(fileProcessingService, times(1)).processFile(expectedPath);
    }
}
