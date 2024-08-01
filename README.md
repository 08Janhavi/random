import bbejeck.nio.files.event.PathEvent;
import bbejeck.nio.files.event.PathEvents;
import com.google.common.eventbus.EventBus;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/15/12
 * Time: 10:56 PM
 */

public class DirectoryEventWatcherImpl implements DirectoryEventWatcher {

    private FutureTask<Integer> watchTask;
    private EventBus eventBus;
    private WatchService watchService;
    private volatile boolean keepWatching = true;
    private Path startPath;


    public DirectoryEventWatcherImpl(EventBus eventBus, Path startPath) {
        this.eventBus = Objects.requireNonNull(eventBus);
        this.startPath = Objects.requireNonNull(startPath);
    }

    @Override
    public void start() throws IOException {
        initWatchService();
        registerDirectories();
        createWatchTask();
        startWatching();
    }

    @Override
    public boolean isRunning() {
        return watchTask != null && !watchTask.isDone() && !watchTask.isCancelled();
    }

    @Override
    public void stop() {
        keepWatching = false;
        if (watchTask != null) {
            watchTask.cancel(true);
        }
    }


    private void createWatchTask() {
        watchTask = new FutureTask<>(new Callable<Integer>() {
            private int totalEventCount;

            @Override
            public Integer call() throws Exception {
                while (keepWatching) {
                    WatchKey watchKey = watchService.poll(10, TimeUnit.SECONDS);
                    if (watchKey != null) {
                        List<WatchEvent<?>> events = watchKey.pollEvents();
                        Path watched = (Path) watchKey.watchable();
                        PathEvents pathEvents = new PathEvents(watchKey.isValid(), watched);
                        for (WatchEvent event : events) {
                            pathEvents.add(new PathEvent((Path) event.context(), event.kind()));
                            totalEventCount++;
                        }
                        watchKey.reset();
                        eventBus.post(pathEvents);
                    }
                }
                return totalEventCount;
            }
        });
    }

    private void startWatching() {
        new Thread(watchTask).start();
    }

    private void registerDirectories() throws IOException {
        Files.walkFileTree(startPath, new WatchServiceRegisteringVisitor());
    }

    private WatchService initWatchService() throws IOException {
        if (watchService == null) {
            watchService = FileSystems.getDefault().newWatchService();
        }
        return watchService;
    }

    private class WatchServiceRegisteringVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            return FileVisitResult.CONTINUE;
        }
    }
}


import bbejeck.nio.files.BaseFileTest;
import bbejeck.nio.files.event.PathEventContext;
import bbejeck.nio.files.event.PathEventSubscriber;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 2/20/12
 * Time: 10:32 PM
 */
//TODO add Test with Phaser to test adding deleting and updating
//TODO add directory on the fly then start watching and add file confirm added to new dir
public class DirectoryEventWatcherImplTest extends BaseFileTest {

    private EventBus eventBus;
    private DirectoryEventWatcherImpl dirWatcher;
    private CountDownLatch doneSignal;
    private TestSubscriber subscriber;

    @Before
    public void setUp() throws Exception {
        createPaths();
        cleanUp();
        createDirectories();
        eventBus = new EventBus();
        dirWatcher = new DirectoryEventWatcherImpl(eventBus, basePath);
        dirWatcher.start();
        subscriber = new TestSubscriber();
        eventBus.register(subscriber);
        doneSignal = new CountDownLatch(1);
    }

    @Test
    public void testDirectoryForWrittenEvents() throws Exception {
        generateFile(basePath.resolve("newTextFile.txt"), 10);
        generateFile(basePath.resolve("newTextFileII.txt"), 10);
        generateFile(basePath.resolve("newTextFileIII.txt"), 10);
        doneSignal.await();
        assertThat(subscriber.getPathEvents().size(), is(1));
        PathEventContext event = subscriber.getPathEvents().get(0);
        assertThat(event.getWatchedDirectory(), is(basePath));
        assertThat(event.getEvents().size(), is(3));
        assertThat(dirWatcher.isRunning(), is(true));
    }


    @After
    public void tearDown() throws Exception {
        dirWatcher.stop();
    }


    private class TestSubscriber implements PathEventSubscriber {
        List<PathEventContext> pathEvents = new ArrayList<>();

        @Override
        @Subscribe
        public void handlePathEvents(PathEventContext pathEventContext) {
            pathEvents.add(pathEventContext);
            doneSignal.countDown();
        }

        public List<PathEventContext> getPathEvents() {
            return pathEvents;
        }
    }
}



package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.util.logging.Logger;

@Service
public class DirectoryMonitorService {

    private final Path directory;
    private final FileProcessingService fileProcessingService;
//    private final WatchService watchService;
    private final Logger logger = Logger.getLogger(DirectoryMonitorService.class.getName());

    @Autowired
    public DirectoryMonitorService(FileProcessingService fileProcessingService) {
        this.directory = Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files");  // Directory to monitor
        this.fileProcessingService = fileProcessingService;
//        this.watchService=watchService;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectoryMonitorServiceTest {

    @Mock
    private FileProcessingService fileProcessingService;

    @Mock
    private WatchService watchService;

    @InjectMocks
    private DirectoryMonitorService directoryMonitorService;

    private Path directory;

    @BeforeEach
    void setUp() {
        directory = Paths.get("C:\\Users\\singhjan\\Downloads\\demo\\demo\\src\\main\\java\\com\\example\\demo\\input_files");
    }

    @Test
    void testWatchDirectory() throws Exception {
        // Mock WatchKey and WatchEvent
        WatchKey mockWatchKey = mock(WatchKey.class);
        WatchEvent<Path> mockWatchEvent = mock(WatchEvent.class);

        // Mock behavior of pollEvents and watchKey
        when(mockWatchEvent.context()).thenReturn(Paths.get("newFile.txt"));
        when(mockWatchEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
        when(mockWatchKey.pollEvents()).thenReturn(List.of(mockWatchEvent));
        when(mockWatchKey.reset()).thenReturn(true);
        when(watchService.take()).thenReturn(mockWatchKey);

        // Run the watchDirectory method in a separate thread
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                directoryMonitorService.watchDirectory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Allow some time for the monitoring process to detect the event
        TimeUnit.SECONDS.sleep(1);

        // Verify that the fileProcessingService's processFile method was called with the correct path
        ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        verify(fileProcessingService).processFile(pathCaptor.capture());
        assert pathCaptor.getValue().equals(directory.resolve("newFile.txt"));

        // Stop the directory monitoring
        verify(mockWatchKey).reset();
    }
}
