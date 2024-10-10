public void deleteFileColumn(Table table) throws Exception {
        logger.info("Deleting file column for table: {}", table.tableName());

        // Step 1: Get the db_table_id from lineage_data_db_tables
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
        String dbTableId = jdbcTemplate.queryForObject(findTableQuery, String.class, table.databaseName(), table.tableName());

        if (dbTableId == null) {
            throw new Exception("Table not found");
        }

        // Step 2: Iterate over each table column to find the associated db_column_id
        for (TableColumn tableColumn : table.tableColumns()) {
            logger.info("Processing column: {}", tableColumn.columnName());

            // Get db_column_id for the current table column
            String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";
            String dbColumnId = jdbcTemplate.queryForObject(findColumnQuery, String.class, dbTableId, tableColumn.columnName());

            if (dbColumnId == null) {
                throw new Exception("Column not found for column name: " + tableColumn.columnName());
            }

            // Step 3: Iterate over file columns to delete them
            for (FileColumn fileColumn : tableColumn.fileColumns()) {
                logger.info("Deleting file column: {}", fileColumn.columnName());




                // Delete the file column from the lineage_data_file_columns table
                String deleteFileColumnQuery = "DELETE FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ? AND file_column_name = ?";
                int rowsAffected = jdbcTemplate.update(deleteFileColumnQuery, dbColumnId, fileColumn.columnName());

                if (rowsAffected == 0) {
                    throw new Exception("Failed to delete file column: " + fileColumn.columnName());
                }
            }
        }
    }
