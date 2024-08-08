package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name="sample1")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ric;
    private String qdb_ric;
    private String seqno;
    private String exDate;
    private String payDate;
    private String type;
    private String typeCode;
    private String amount;
    private String ccy;
    private String stk_Ccy;
    private String bl_Event;
    private String declareDate;
    private String recordDate;
    private String fiscalYeDate;
    private String source;
    private String qdb_Type_Code;
    private String pdpId;
    private String takara_Amnt;
    private String takara_CpNetAmnt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRic() {
        return ric;
    }

    public void setRic(String ric) {
        this.ric = ric;
    }

    public String getQdb_ric() {
        return qdb_ric;
    }

    public void setQdb_ric(String qdb_ric) {
        this.qdb_ric = qdb_ric;
    }

    public String getSeqno() {
        return seqno;
    }

    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }

    public String getExDate() {
        return exDate;
    }

    public void setExDate(String exDate) {
        this.exDate = exDate;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getStk_Ccy() {
        return stk_Ccy;
    }

    public void setStk_Ccy(String stk_Ccy) {
        this.stk_Ccy = stk_Ccy;
    }

    public String getBl_Event() {
        return bl_Event;
    }

    public void setBl_Event(String bl_Event) {
        this.bl_Event = bl_Event;
    }

    public String getDeclareDate() {
        return declareDate;
    }

    public void setDeclareDate(String declareDate) {
        this.declareDate = declareDate;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getFiscalYeDate() {
        return fiscalYeDate;
    }

    public void setFiscalYeDate(String fiscalYeDate) {
        this.fiscalYeDate = fiscalYeDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getQdb_Type_Code() {
        return qdb_Type_Code;
    }

    public void setQdb_Type_Code(String qdb_Type_Code) {
        this.qdb_Type_Code = qdb_Type_Code;
    }

    public String getPdpId() {
        return pdpId;
    }

    public void setPdpId(String pdpId) {
        this.pdpId = pdpId;
    }

    public String getTakara_Amnt() {
        return takara_Amnt;
    }

    public void setTakara_Amnt(String takara_Amnt) {
        this.takara_Amnt = takara_Amnt;
    }

    public String getTakara_CpNetAmnt() {
        return takara_CpNetAmnt;
    }

    public void setTakara_CpNetAmnt(String takara_CpNetAmnt) {
        this.takara_CpNetAmnt = takara_CpNetAmnt;
    }

    public String getGlobalPrimaryEsmp() {
        return globalPrimaryEsmp;
    }

    public void setGlobalPrimaryEsmp(String globalPrimaryEsmp) {
        this.globalPrimaryEsmp = globalPrimaryEsmp;
    }

    public String getRegionalPrimaryEsmp() {
        return regionalPrimaryEsmp;
    }

    public void setRegionalPrimaryEsmp(String regionalPrimaryEsmp) {
        this.regionalPrimaryEsmp = regionalPrimaryEsmp;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getIsAusSplit() {
        return isAusSplit;
    }

    public void setIsAusSplit(String isAusSplit) {
        this.isAusSplit = isAusSplit;
    }

    public String getNxs_Ccy() {
        return nxs_Ccy;
    }

    public void setNxs_Ccy(String nxs_Ccy) {
        this.nxs_Ccy = nxs_Ccy;
    }

    public String getNxs_DvdId() {
        return nxs_DvdId;
    }

    public void setNxs_DvdId(String nxs_DvdId) {
        this.nxs_DvdId = nxs_DvdId;
    }

    public String getNxs_DivType() {
        return nxs_DivType;
    }

    public void setNxs_DivType(String nxs_DivType) {
        this.nxs_DivType = nxs_DivType;
    }

    public String getTakaraUpdated() {
        return takaraUpdated;
    }

    public void setTakaraUpdated(String takaraUpdated) {
        this.takaraUpdated = takaraUpdated;
    }

    public String getGlobalPrimary() {
        return globalPrimary;
    }

    public void setGlobalPrimary(String globalPrimary) {
        this.globalPrimary = globalPrimary;
    }

    public String getEquityRegionalPrimaryListing() {
        return equityRegionalPrimaryListing;
    }

    public void setEquityRegionalPrimaryListing(String equityRegionalPrimaryListing) {
        this.equityRegionalPrimaryListing = equityRegionalPrimaryListing;
    }

    public String getListingType() {
        return listingType;
    }

    public void setListingType(String listingType) {
        this.listingType = listingType;
    }

    private String globalPrimaryEsmp;
    private String regionalPrimaryEsmp;
    private String countryCode;
    private String isAusSplit;
    private String nxs_Ccy;
    private String nxs_DvdId;
    private String nxs_DivType;
    private String takaraUpdated;
    private String globalPrimary;
    private String equityRegionalPrimaryListing;
    private String listingType;
}


package com.example.demo.repository;

import com.example.demo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}


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
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    public void watchDirectory() {
        logger.info("Starting directory monitoring for directory: " + directory.toAbsolutePath());

        try (watchService) {
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


package com.example.demo.service;


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
import java.util.logging.Logger;

@Service
public class FileProcessingService {

    @Autowired
    private TransactionRepository transactionRepository;
    private static final Logger logger = Logger.getLogger(FileProcessingService.class.getName());

    public void processFile(Path filePath) {
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

                transactionRepository.save(transaction);
            }
        } catch (IOException e) {
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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
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

directory.watch.path=C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files

package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
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
        DirectoryMonitorService directoryMonitorService = new DirectoryMonitorService();
        ReflectionTestUtils.setField(directoryMonitorService, "directory", directory);
        ReflectionTestUtils.setField(directoryMonitorService, "fileProcessingService", fileProcessingService);
        ReflectionTestUtils.setField(directoryMonitorService, "watchService", watchService);
        return directoryMonitorService;
    }

    @Test
    public void testWatchDirectory() throws InterruptedException, IOException {
        DirectoryMonitorService directoryMonitorService = setMockers();
        Path relativePath = Paths.get("testFile.txt");
        WatchEvent<Path> mockEvent = mock(WatchEvent.class);
        when(mockEvent.context()).thenReturn(relativePath);
        when(directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE)).thenReturn(watchKey);
        when(watchService.take()).thenReturn(watchKey);
        when(watchKey.pollEvents()).thenReturn(Collections.singletonList(mockEvent));
        when(watchKey.reset()).thenReturn(true);

        AtomicInteger counter = new AtomicInteger();
        when(watchService.take()).thenAnswer(invocation -> {
            if (counter.incrementAndGet() > 1) {
                return null;
            }
            return watchKey;
        });

        directoryMonitorService.watchDirectory();
        Path expectedPath = directory.resolve(relativePath);
        verify(fileProcessingService, times(1)).processFile(expectedPath);
    }

    @Test
    public void testWatchDirectoryWithException() throws IOException {
        DirectoryMonitorService directoryMonitorService = setMockers();
        doThrow(IOException.class).when(directory).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        directoryMonitorService.watchDirectory();
        verifyNoInteractions(fileProcessingService);
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
