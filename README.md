USE rawdata
go
CREATE TABLE dbo.lineage_data_file_columns
(
    db_column_id     varchar(32) NOT NULL,
    file_column_name char(20)    NOT NULL,
    file_name        char(30)    NOT NULL,
    file_source      char(30)    NOT NULL,
    created_by       char(30)    NOT NULL,
    created_date     datetime    DEFAULT getdate() NOT NULL,
    updated_by       char(30)    NULL,
    updated_date     datetime    NULL,
    file_column_id   varchar(32) DEFAULT newid()   NOT NULL,
    CONSTRAINT lineage_da_605665281
    PRIMARY KEY CLUSTERED (file_column_id)
)
LOCK ALLPAGES
go
IF OBJECT_ID('dbo.lineage_data_file_columns') IS NOT NULL
    PRINT '<<< CREATED TABLE dbo.lineage_data_file_columns >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE dbo.lineage_data_file_columns >>>'
go
ALTER TABLE dbo.lineage_data_file_columns
    ADD CONSTRAINT lineage_da_108566699
    FOREIGN KEY (db_column_id)
    REFERENCES dbo.lineage_data_db_table_columns (db_column_id)
go












USE rawdata
go
CREATE TABLE dbo.lineage_data_db_table_columns
(
    db_table_id    varchar(32) NOT NULL,
    db_column_name char(20)    NOT NULL,
    process_name   char(30)    NOT NULL,
    created_by     char(30)    NOT NULL,
    created_date   datetime    DEFAULT getdate() NOT NULL,
    updated_by     char(30)    NULL,
    updated_date   datetime    NULL,
    db_column_id   varchar(32) DEFAULT newid()   NOT NULL,
    CONSTRAINT lineage_da_21280498911
    PRIMARY KEY CLUSTERED (db_column_id)
)
LOCK ALLPAGES
go
IF OBJECT_ID('dbo.lineage_data_db_table_columns') IS NOT NULL
    PRINT '<<< CREATED TABLE dbo.lineage_data_db_table_columns >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE dbo.lineage_data_db_table_columns >>>'
go
ALTER TABLE dbo.lineage_data_db_table_columns
    ADD CONSTRAINT lineage_da_28566414
    FOREIGN KEY (db_table_id)
    REFERENCES dbo.lineage_data_db_tables (db_table_id)
go










USE rawdata
go
CREATE TABLE dbo.lineage_data_db_tables
(
    database_name char(15)    NOT NULL,
    db_table_name char(20)    NOT NULL,
    created_by    char(30)    NOT NULL,
    created_date  datetime    DEFAULT getdate() NOT NULL,
    updated_by    char(30)    NULL,
    updated_date  datetime    NULL,
    db_table_id   varchar(32) DEFAULT newid()   NOT NULL,
    CONSTRAINT lineage_da_20640496631
    PRIMARY KEY CLUSTERED (db_table_id)
)
LOCK ALLPAGES
go
IF OBJECT_ID('dbo.lineage_data_db_tables') IS NOT NULL
    PRINT '<<< CREATED TABLE dbo.lineage_data_db_tables >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE dbo.lineage_data_db_tables >>>'
go
