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
    long duration;
    public void processFile(Path filePath){
        try(BufferedReader reader= new BufferedReader(new FileReader(String.valueOf(filePath)))){
            String line;
            System.out.println(reader.readLine());
            while((line=reader.readLine())!=null){
                String[] fields=line.split("\\|");
                Transaction transaction=new Transaction();
                transaction.setRic(fields[0]);
                transaction.setQdb_ric(fields[1]);
                transaction.setSeqno(fields[2]);
                transaction.setExDate(fields[3]);
                transaction.setPayDate(fields[4]);
                transaction.setType(fields[5]);
                transaction.setTypeCode(fields[6]);
                transaction.setAmount(fields[7]);
                transaction.setCcy(fields[8]);
                transaction.setCcy(fields[9]);
                transaction.setBl_Event(fields[10]);
                transaction.setDeclareDate(fields[11]);
                transaction.setRecordDate(fields[12]);
                transaction.setFiscalYeDate(fields[13]);
                transaction.setSource(fields[14]);
                transaction.setQdb_Type_Code(fields[15]);
                transaction.setPdpId(fields[16]);
                transaction.setTakara_Amnt(fields[17]);
                transaction.setTakara_CpNetAmnt(fields[18]);
                transaction.setGlobalPrimaryEsmp(fields[19]);
                transaction.setRegionalPrimaryEsmp(fields[20]);
                transaction.setCountryCode(fields[21]);
                transaction.setIsAusSplit(fields[22]);
                transaction.setNxs_Ccy(fields[23]);
                transaction.setNxs_DvdId(fields[24]);
                transaction.setNxs_DivType(fields[25]);
                transaction.setTakaraUpdated(fields[26]);
                transaction.setGlobalPrimary(fields[27]);
                transaction.setEquityRegionalPrimaryListing(fields[28]);
                transaction.setListingType(fields[29]);
                transactionRepository.save(transaction);
            }
        } catch (IOException e){
            logger.info("hiiii reached in catch");
            e.printStackTrace();
        } finally {
            moveFileToProcessed(filePath);
        }
    }

    private void moveFileToProcessed(Path filePath) {
        try {
            Path processedDir = Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\processed_files");
            Files.move(filePath, processedDir.resolve(filePath.getFileName()));
            logger.info("File processed successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
