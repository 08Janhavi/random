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
            // Read and discard the header line if present
            reader.readLine();

            while((line=reader.readLine())!=null){
                String[] fields = line.split("\\|");

                // Validate the number of fields
                if(fields.length != 30) {
                    logger.warning("Unexpected number of fields in line: " + line);
                    continue; // Skip this line
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
        return (field == null || field.trim().isEmpty()) ? "N/A" : field;
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
