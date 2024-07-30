package com.example.batch.service;

import com.example.batch.entity.YourEntity;
import com.example.batch.repository.YourEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileProcessorService {

    private static final int EXPECTED_LENGTH = 3; // Reflects the number of fields

    @Autowired
    private YourEntityRepository repository;

    public void processFile(Path filePath) {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data.length < EXPECTED_LENGTH) {
                    // Handle unexpected length
                    System.err.println("Unexpected data length: " + data.length);
                    continue;
                }

                YourEntity entity = new YourEntity();
                entity.setName(data[0].trim());

                // Handle potential null or invalid age
                try {
                    Integer age = Integer.parseInt(data[1].trim());
                    entity.setAge(age);
                } catch (NumberFormatException e) {
                    // Log the error and continue
                    System.err.println("Invalid age value: " + data[1].trim());
                    continue;
                }

                entity.setEmail(data[2].trim());

                // Validate and handle null or unexpected values
                if (isValid(entity)) {
                    repository.save(entity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            moveFileToProcessed(filePath);
        }
    }

    private boolean isValid(YourEntity entity) {
        // Implement validation logic
        return entity.getName() != null && !entity.getName().isEmpty() &&
                entity.getAge() != null &&
                entity.getEmail() != null && !entity.getEmail().isEmpty();
    }

    private void moveFileToProcessed(Path filePath) {
        try {
            Path processedDir = Paths.get("C:\\Users\\suraj\\Downloads\\testing\\src\\main\\java\\com\\example\\batch\\processed_files");
            Files.move(filePath, processedDir.resolve(filePath.getFileName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
