package com.example.demo.service;

import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileProcessingServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private Path processedDirectory;

    @InjectMocks
    private FileProcessingService fileProcessingService;

    private Path filePath;

    @BeforeEach
    void setUp() {
        filePath = mock(Path.class);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processFile_FileAlreadyProcessed() throws IOException {
        when(filePath.getFileName()).thenReturn(mock(Path.class));
        when(processedDirectory.resolve(any(Path.class))).thenReturn(mock(Path.class));
        when(Files.exists(any(Path.class))).thenReturn(true);

        fileProcessingService.processFile(filePath);

        verify(transactionRepository, never()).saveAll(anyList());
        verify(fileProcessingService, times(1)).moveFileToProcessed(filePath);
    }

    @Test
    void processFile_FileSuccessfullyProcessed() throws IOException {
        when(filePath.getFileName()).thenReturn(mock(Path.class));
        when(processedDirectory.resolve(any(Path.class))).thenReturn(mock(Path.class));
        when(Files.exists(any(Path.class))).thenReturn(false);

        BufferedReader reader = mock(BufferedReader.class);
        when(reader.readLine()).thenReturn("header", "line1|field|data", (String) null);
        whenNew(BufferedReader.class).withArguments(any(FileReader.class)).thenReturn(reader);

        fileProcessingService.processFile(filePath);

        verify(transactionRepository, times(1)).saveAll(anyList());
        verify(fileProcessingService, times(1)).moveFileToProcessed(filePath);
    }

    @Test
    void processFile_ErrorReadingFile() throws IOException {
        when(filePath.getFileName()).thenReturn(mock(Path.class));
        when(processedDirectory.resolve(any(Path.class))).thenReturn(mock(Path.class));
        when(Files.exists(any(Path.class))).thenReturn(false);

        BufferedReader reader = mock(BufferedReader.class);
        when(reader.readLine()).thenThrow(new IOException("Test exception"));

        assertThrows(IOException.class, () -> fileProcessingService.processFile(filePath));

        verify(transactionRepository, never()).saveAll(anyList());
        verify(fileProcessingService, times(1)).moveFileToProcessed(filePath);
    }

    @Test
    void moveFileToProcessed_Success() throws IOException {
        when(filePath.getFileName()).thenReturn(mock(Path.class));
        when(processedDirectory.resolve(any(Path.class))).thenReturn(mock(Path.class));
        when(Files.exists(any(Path.class))).thenReturn(false);

        fileProcessingService.moveFileToProcessed(filePath);

        verify(Files, times(1)).move(any(Path.class), any(Path.class));
    }

    @Test
    void moveFileToProcessed_FileAlreadyExists() throws IOException {
        when(filePath.getFileName()).thenReturn(mock(Path.class));
        when(processedDirectory.resolve(any(Path.class))).thenReturn(mock(Path.class));
        when(Files.exists(any(Path.class))).thenReturn(true);

        fileProcessingService.moveFileToProcessed(filePath);

        verify(Files, never()).move(any(Path.class), any(Path.class));
    }
}
