<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.3.2</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.nomura.im</groupId>
	<artifactId>im-data-lineage-exporter</artifactId>
	<version>1.0.0-SNAPSHOT</version>
	<name>im-data-lineage</name>
	<description>Project for capturing data lineage in IM</description>
	
	<distributionManagement>
        <repository>
            <id>instrument-master-releases</id>
            <name>Instrument Master Releases</name>
            <url>http://fid-nexus-master.nomura.com/ETCB/nexus/content/repositories/instrument-master-releases</url>
        </repository>
        <snapshotRepository>
            <id>instrument-master-snapshots</id>
            <name>Instrument Master Snapshots</name>
            <url>http://fid-nexus-master.nomura.com/ETCB/nexus/content/repositories/instrument-master-snapshots</url>
            <uniqueVersion>false</uniqueVersion>
        </snapshotRepository>
    </distributionManagement>
    
	<scm>
		<connection/>
		<developerConnection/>
		<tag/>
		<url/>
	</scm>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-jdbc</artifactId>
		</dependency>

<!--		<dependency>-->
<!--			<groupId>net.sourceforge.jtds</groupId>-->
<!--			<artifactId>jtds</artifactId>-->
<!--			<version>1.3.1</version>-->
<!--		</dependency>-->

		<dependency>
			<groupId>sybase</groupId>
			<artifactId>jconnect</artifactId>
			<version>7.0</version>
		</dependency>

<!--		<dependency>-->
<!--			<groupId>com.sybase</groupId>-->
<!--			<artifactId>sybase-jdbc</artifactId>-->
<!--			<version>1.0</version>-->
<!--			<scope>compile</scope>-->
<!--		</dependency>-->

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.apache.poi</groupId>
			<artifactId>poi</artifactId>
			<version>5.3.0</version>
		</dependency>
		<dependency>
			<groupId>org.apache.poi</groupId>
			<artifactId>poi-ooxml</artifactId>
			<version>5.3.0</version>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
			<exclusions>
				<exclusion>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-starter-logging</artifactId>
				</exclusion>
			</exclusions>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-log4j2</artifactId>
		</dependency>

		<dependency>
			<groupId>org.apache.logging.log4j</groupId>
			<artifactId>log4j-core</artifactId>
			<version>2.19.0</version>
		</dependency>
		<dependency>
			<groupId>org.apache.logging.log4j</groupId>
			<artifactId>log4j-api</artifactId>
			<version>2.19.0</version>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
			<plugin>
				<artifactId>maven-assembly-plugin</artifactId>
				<executions>
					<execution>
						<phase>package</phase>
						<goals>
							<goal>single</goal>
						</goals>
						<configuration>
							<appendAssemblyId>false</appendAssemblyId>	
							<descriptors>
								<descriptor>assembly.xml</descriptor>
							</descriptors>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>

</project>
