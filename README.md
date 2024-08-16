package com.example.demo;

import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.FileProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileProcessingServiceTest {

    private Path processedDirectory;
    private Path tempFile;

    private FileProcessingService fileProcessingService;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setup() throws IOException {
        processedDirectory = Files.createTempDirectory("processed-dir");
        tempFile = Files.createTempFile("test-file", ".txt");
        Files.write(tempFile, "header\nfield1|field2|field3|field4|field5|field6|field7|field8|field9|field10|field11|field12|field13|field14|field15|field16|field17|field18|field19|field20|field21|field22|field23|field24|field25|field26|field27|field28|field29|field30".getBytes());

        fileProcessingService = new FileProcessingService();
        ReflectionTestUtils.setField(fileProcessingService, "processedDirectory", processedDirectory);
        fileProcessingService.transactionRepository = transactionRepository;
    }

    @Test
    public void testProcessFile_ValidFile() throws IOException {
        fileProcessingService.processFile(tempFile);
        verify(transactionRepository, atLeastOnce()).save(any(Transaction.class));
        assertFileMovedToProcessed();
    }

    @Test
    public void testProcessFile_InvalidFieldCount() throws IOException {
        Files.write(tempFile, "header\nfield1|field2|field3".getBytes());

        fileProcessingService.processFile(tempFile);

        verify(transactionRepository, never()).save(any(Transaction.class));
        assertFileMovedToProcessed();
    }

    @Test
    public void testProcessFile_FileNotFound() throws IOException {
        Path nonExistentFile = tempFile.resolve("non-existent-file.txt");

        fileProcessingService.processFile(nonExistentFile);

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    private void assertFileMovedToProcessed() throws IOException {
        assertTrue(Files.exists(processedDirectory.resolve(tempFile.getFileName())));
    }
}







package com.example.demo;

import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.FileProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class FileProcessingServiceTest {

    @Mock
    private Path processedDirectory;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FileProcessingService fileProcessingService;

    private Path tempFile;

    @BeforeEach
    public void setup() throws IOException {
        tempFile = Files.createTempFile("test-file", ".txt");
        Files.write(tempFile, "header\nfield1|field2|field3|field4|field5|field6|field7|field8|field9|field10|field11|field12|field13|field14|field15|field16|field17|field18|field19|field20|field21|field22|field23|field24|field25|field26|field27|field28|field29|field30".getBytes());

        when(processedDirectory.toUri()).thenReturn(Paths.get("processed-dir").toUri());
        when(processedDirectory.resolve(any(Path.class))).thenReturn(Paths.get("processed-dir/" + tempFile.getFileName()));

        ReflectionTestUtils.setField(fileProcessingService, "processedDirectory", processedDirectory);
    }

    @Test
    public void testProcessFile_ValidFile() throws IOException {
        fileProcessingService.processFile(tempFile);
        verify(transactionRepository, atLeastOnce()).save(any(Transaction.class));
        assertFileMovedToProcessed();
    }

    @Test
    public void testProcessFile_InvalidFieldCount() throws IOException {
        Files.write(tempFile, "header\nfield1|field2|field3".getBytes());

        fileProcessingService.processFile(tempFile);

        verify(transactionRepository, never()).save(any(Transaction.class));
        assertFileMovedToProcessed();
    }

    @Test
    public void testProcessFile_FileNotFound() throws IOException {
        Path nonExistentFile = Paths.get("non-existent-file.txt");

        fileProcessingService.processFile(nonExistentFile);

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    private void assertFileMovedToProcessed() throws IOException {
        assertTrue(Files.exists(Paths.get("processed-dir/" + tempFile.getFileName())));
    }
}



