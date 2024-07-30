package com.example.batch;

import com.example.batch.service.DirectoryMonitorService;
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
