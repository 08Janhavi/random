package com.example.demo.repository;

import com.example.demo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

}


package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Logger;

@Service
public class DirectoryMonitorService {

    private final Path directory;
    private final FileProcessingService fileProcessingService;
    private final WatchService watchService;
    private final Logger logger = Logger.getLogger(DirectoryMonitorService.class.getName());

    @Autowired
    public DirectoryMonitorService(FileProcessingService fileProcessingService) throws IOException {
        this.directory = Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files");  // Directory to monitor
        this.fileProcessingService = fileProcessingService;
        this.watchService=FileSystems.getDefault().newWatchService();
    }

    public DirectoryMonitorService(FileProcessingService fileProcessingService,WatchService watchService)  {
        this.directory = Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files");  // Directory to monitor
        this.fileProcessingService = fileProcessingService;
        this.watchService=watchService;
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

package com.example.demo.service;

import ch.qos.logback.classic.Logger;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileProcessingService {

    @Autowired
    private TransactionRepository transactionRepository;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FileProcessingService.class.getName());

    public void processFile(Path filePath){
        try(BufferedReader reader= new BufferedReader(new FileReader(String.valueOf(filePath)))){
            String line;
            reader.readLine();

            while((line=reader.readLine())!=null){
                String[] fields = line.split("\\|");

                // Validate the number of fields
                if(fields.length != 30) {
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

                transactionRepository.save(transaction);
            }
        } catch (IOException e){
            logger.severe("Error reading file: " + e.getMessage());
            e.printStackTrace();
        } finally {
            moveFileToProcessed(filePath);
        }
    }

    private String validateField(String field) {
        return (field.trim().isEmpty()) ? "N/A" : field;
    }

    private void moveFileToProcessed(Path filePath) {
        try {
            Path processedDir = Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\processed_files");
            Files.move(filePath, processedDir.resolve(filePath.getFileName()));
            logger.info("File processed successfully");
        } catch (IOException e) {
            logger.severe("Error moving file to processed directory: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package com.example.demo;

import com.example.demo.service.DirectoryMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class StartupConfig {

    @Autowired
    private DirectoryMonitorService directoryMonitorService;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println("Starting directory monitoring...");
            directoryMonitorService.watchDirectory();
        };
    }
}


spring.application.name=demo
spring.datasource.url=jdbc:sqlite:data.db
spring.datasource.driver-class-name=org.sqlite.JDBC

spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.leak-detection-threshold=15000

spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update



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
