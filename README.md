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
