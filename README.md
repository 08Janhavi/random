OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
Jul 31, 2024 12:43:34 PM com.example.demo.service.DirectoryMonitorService watchDirectory
INFO: Starting directory monitoring for directory: C:\Users\singhjan\Downloads\demo\demo\src\main\java\com\example\demo\input_files
Jul 31, 2024 12:43:35 PM com.example.demo.service.DirectoryMonitorService watchDirectory
SEVERE: Error in directory monitoring: null


Wanted but not invoked:
fileProcessingService.processFile(
    C:\Users\singhjan\Downloads\demo\demo\src\main\java\com\example\demo\input_files\testFile.txt
);
-> at com.example.demo.service.FileProcessingService.processFile(FileProcessingService.java:24)
Actually, there were zero interactions with this mock.

Wanted but not invoked:
fileProcessingService.processFile(
    C:\Users\singhjan\Downloads\demo\demo\src\main\java\com\example\demo\input_files\testFile.txt
);
-> at com.example.demo.service.FileProcessingService.processFile(FileProcessingService.java:24)
Actually, there were zero interactions with this mock.

	at com.example.demo.service.FileProcessingService.processFile(FileProcessingService.java:24)
	at com.example.demo.DirectoryMonitorServiceTest.testWatchDirectory(DirectoryMonitorServiceTest.java:67)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
