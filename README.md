@Test
    public void testWatchDirectoryWithException() throws IOException {
        DirectoryMonitorService directoryMonitorService = setMockers();

        // Simulate an exception being thrown when trying to register the directory
        doThrow(IOException.class).when(directory).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        Logger logger = Logger.getLogger(DirectoryMonitorService.class.getName());

        // Capture the logging output
        ReflectionTestUtils.setField(directoryMonitorService, "logger", logger);

        // Run the watchDirectory method
        assertThrows(IOException.class, directoryMonitorService::watchDirectory);

        // Verify that no further interactions with the fileProcessingService happened
        verifyNoInteractions(fileProcessingService);
    }
