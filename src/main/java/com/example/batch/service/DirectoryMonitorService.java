package com.example.batch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.util.logging.Logger;

@Service
public class DirectoryMonitorService {

    private final Path directory;
    private final FileProcessorService fileProcessorService;
    private static final Logger logger = Logger.getLogger(DirectoryMonitorService.class.getName());

    @Autowired
    public DirectoryMonitorService(FileProcessorService fileProcessorService) {
        this.directory = Paths.get("C:\\Users\\suraj\\Downloads\\testing\\src\\main\\java\\com\\example\\batch\\files");  // Directory to monitor
        this.fileProcessorService = fileProcessorService;
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
                    fileProcessorService.processFile(filePath);
                }
                key.reset();
            }
        } catch (Exception e) {
            logger.severe("Error in directory monitoring: " + e.getMessage());
        }
    }
}