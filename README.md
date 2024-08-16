package com.example.demo.service;

import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Service
public class FileProcessingService {
    @Value("${processed.directory}")
    private Path processedDirectory;

    @Autowired
    private TransactionRepository transactionRepository;
    private static final Logger logger = Logger.getLogger(FileProcessingService.class.getName());

    public void processFile(Path filePath) {
        Path processedDir=Paths.get(processedDirectory.toUri());
        Path targetFilePath=processedDir.resolve(filePath.getFileName());
        if (Files.exists(targetFilePath)){
            logger.info("File already processed " + filePath.getFileName());
            moveFileToProcessed(filePath);
            return;
        }
        List<Transaction> transactions=new ArrayList<>();
//        ExecutorService executorService= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try (BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(filePath)))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");

                if (fields.length != 30) {
                    logger.warning("Unexpected number of fields in line: " + line);
                    continue;
                }

                Transaction transaction = new Transaction();
                transaction.setRic(validateField(fields[0]));
                transaction.setQdb_ric(validateField(fields[1]));
                transaction.setSeqno(validateField(fields[2]));
                transaction.setExDate(validateField(fields[3]));
                transaction.setPayDate(validateField(fields[4]));
                transaction.setType(validateField(fields[5]));
                transaction.setTypeCode(validateField(fields[6]));
                transaction.setAmount(validateField(fields[7]));
                transaction.setCcy(validateField(fields[8]));
                transaction.setBl_Event(validateField(fields[10]));
                transaction.setDeclareDate(validateField(fields[11]));
                transaction.setRecordDate(validateField(fields[12]));
                transaction.setFiscalYeDate(validateField(fields[13]));
                transaction.setSource(validateField(fields[14]));
                transaction.setQdb_Type_Code(validateField(fields[15]));
                transaction.setPdpId(validateField(fields[16]));
                transaction.setTakara_Amnt(validateField(fields[17]));
                transaction.setTakara_CpNetAmnt(validateField(fields[18]));
                transaction.setGlobalPrimaryEsmp(validateField(fields[19]));
                transaction.setRegionalPrimaryEsmp(validateField(fields[20]));
                transaction.setCountryCode(validateField(fields[21]));
                transaction.setIsAusSplit(validateField(fields[22]));
                transaction.setNxs_Ccy(validateField(fields[23]));
                transaction.setNxs_DvdId(validateField(fields[24]));
                transaction.setNxs_DivType(validateField(fields[25]));
                transaction.setTakaraUpdated(validateField(fields[26]));
                transaction.setGlobalPrimary(validateField(fields[27]));
                transaction.setEquityRegionalPrimaryListing(validateField(fields[28]));
                transaction.setListingType(validateField(fields[29]));
                transactions.add(transaction);
                if(transactions.size()>=1000){
                    transactionRepository.saveAll(transactions);
                    transactions.clear();
                }
            }
        } catch (IOException e) {
            logger.severe("Error reading file: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if(!transactions.isEmpty()){
                transactionRepository.saveAll(transactions);
            }
            moveFileToProcessed(filePath);
        }
    }

    private String validateField(String field) {
        return (field.trim().isEmpty()) ? "N/A" : field;
    }

    private void moveFileToProcessed(Path filePath) {
        try {
            Path processedDir = Paths.get(processedDirectory.toUri());
            Path targetPath= processedDir.resolve(filePath.getFileName());
            if(Files.exists(targetPath)){}
            else{
                Files.move(filePath, targetPath);
                logger.info("File processed successfully");
            }
        } catch (IOException e) {
            logger.severe("Error moving file to processed directory: " + e.getMessage());
            e.printStackTrace();
        }
    }
}







package com.example.demo;

import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    }

    private FileProcessingService setMockers() {
        FileProcessingService fileProcessingService = new FileProcessingService();
        ReflectionTestUtils.setField(fileProcessingService, "processedDirectory", processedDirectory);
        return fileProcessingService;
    }

    @Test
    public void testProcessFile_ValidFile() throws IOException {
        FileProcessingService fileProcessingService = setMockers();
        fileProcessingService.processFile(tempFile);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        assertFileMovedToProcessed();
    }

    @Test
    public void testProcessFile_InvalidFieldCount() throws IOException {
        FileProcessingService fileProcessingService = setMockers();

        Files.write(tempFile, "header\nfield1|field2|field3".getBytes());

        fileProcessingService.processFile(tempFile);

        verify(transactionRepository, times(0)).save(any(Transaction.class));
        assertFileMovedToProcessed();
    }

    @Test
    public void testProcessFile_FileNotFound() throws IOException {
        FileProcessingService fileProcessingService = setMockers();
        Path nonExistentFile = Paths.get("non-existent-file.txt");

        fileProcessingService.processFile(nonExistentFile);

        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    private void assertFileMovedToProcessed() throws IOException {
        assert Files.exists(processedDirectory.resolve(tempFile.getFileName()));
    }
}
