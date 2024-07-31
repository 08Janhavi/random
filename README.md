package com.example.demo.service;

import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileProcessingServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FileProcessingService fileProcessingService;

    private Path tempFile;

    @BeforeEach
    public void setup() throws IOException {
        // Create a temporary file for testing
        tempFile = Files.createTempFile("test-file", ".txt");
        Files.write(tempFile, "header\nfield1|field2|field3|field4|field5|field6|field7|field8|field9|field10|field11|field12|field13|field14|field15|field16|field17|field18|field19|field20|field21|field22|field23|field24|field25|field26|field27|field28|field29|field30".getBytes());
    }

    @Test
    public void testProcessFile_ValidFile() throws IOException {
        fileProcessingService.processFile(tempFile);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        assertFileMovedToProcessed();
    }

    @Test
    public void testProcessFile_InvalidFieldCount() throws IOException {
        // Write a line with invalid field count
        Files.write(tempFile, "header\nfield1|field2|field3".getBytes());

        fileProcessingService.processFile(tempFile);

        verify(transactionRepository, times(0)).save(any(Transaction.class));
        assertFileMovedToProcessed();
    }

    @Test
    public void testProcessFile_FileNotFound() {
        Path nonExistentFile = Paths.get("non-existent-file.txt");

        fileProcessingService.processFile(nonExistentFile);

        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    private void assertFileMovedToProcessed() throws IOException {
        Path processedDir = Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\processed_files");
        assert Files.exists(processedDir.resolve(tempFile.getFileName()));
    }
}
