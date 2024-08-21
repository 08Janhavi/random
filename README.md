# Log4j2 Properties Configuration

# Set the logging level for the root logger
status = WARN

# Define appenders
appender.console.type = Console
appender.console.name = ConsoleAppender
appender.console.target = SYSTEM_OUT
appender.console.layout.type = PatternLayout
appender.console.layout.pattern = %d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n

appender.file.type = File
appender.file.name = FileAppender
appender.file.fileName = logs/app.log
appender.file.layout.type = PatternLayout
appender.file.layout.pattern = %d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n

# Define the root logger
rootLogger.level = info
rootLogger.appenderRefs = ConsoleAppender, FileAppender
rootLogger.appenderRef.ConsoleAppender.ref = ConsoleAppender
rootLogger.appenderRef.FileAppender.ref = FileAppender
