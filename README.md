public void saveLineageData(Table table) {
        logger.info("Saving lineage data for table : {}", table.tableName());

        // Save the table (insert or update)
        String saveTableQuery = "INSERT INTO lineage_data_db_table (database_name, db_table_name) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE db_table_name = ?";
        jdbcTemplate.update(saveTableQuery, table.databaseName(), table.tableName(), table.tableName());

        // Save the table columns
        for (TableColumn column : table.tableColumns()) {
            logger.info("Saving column: {}", column.columnName());

            String saveColumnQuery = "INSERT INTO lineage_data_db_table_columns (db_table_id, db_column_name, process_name) " +
                    "VALUES ((SELECT db_table_id FROM lineage_data_db_table WHERE db_table_name = ?), ?, ?) " +
                    "ON DUPLICATE KEY UPDATE db_column_name = ?, process_name = ?";
            jdbcTemplate.update(saveColumnQuery, table.tableName(), column.columnName(), column.processName(), column.columnName(), column.processName());

            // Save the file columns
            for (FileColumn fileColumn : column.fileColumns()) {
                logger.info("Saving file column: {}", fileColumn.columnName());

                String saveFileColumnQuery = "INSERT INTO lineage_data_file_columns (db_column_id, file_column_name, file_name, file_source) " +
                        "VALUES ((SELECT db_column_id FROM lineage_data_db_table_columns WHERE db_column_name = ?), ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE file_column_name = ?, file_name = ?, file_source = ?";
                jdbcTemplate.update(saveFileColumnQuery, column.columnName(), fileColumn.columnName(), fileColumn.fileName(), fileColumn.fileSource(),
                        fileColumn.columnName(), fileColumn.fileName(), fileColumn.fileSource());
            }
        }
    }
