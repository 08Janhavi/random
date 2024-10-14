public void saveLineageData(Table table) {
        logger.info("Saving lineage data for table: {}", table.tableName());

        // Step 1: Get the db_table_id from lineage_data_db_table
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_tables WHERE database_name = ? AND db_table_name = ?";
        String dbTableId = jdbcTemplate.queryForObject(findTableQuery, String.class, table.databaseName(), table.tableName());

        if (dbTableId == null) {
            // Insert the new table if it doesn't exist
            String insertTableQuery = "INSERT INTO rawdata.dbo.lineage_data_db_tables (database_name, db_table_name,created_by) VALUES (?, ?,?)";
            jdbcTemplate.update(insertTableQuery, table.databaseName(), table.tableName(),CREATED_BY);

            // Fetch the newly inserted db_table_id
            dbTableId = jdbcTemplate.queryForObject(findTableQuery, String.class, table.databaseName(), table.tableName(),CREATED_BY);
        }

        // Step 2: For each table column, insert or update data in lineage_data_db_table_columns
        for (TableColumn column : table.tableColumns()) {
            logger.info("Saving column: {}", column.columnName());

            // Check if the column already exists by fetching db_column_id
            String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";
            String dbColumnId = null;
            try{
                dbColumnId = jdbcTemplate.queryForObject(findColumnQuery, String.class, dbTableId, column.columnName());
            }
            catch(Exception e){
                e.printStackTrace();
            }

            if (dbColumnId == null) {
                // Insert a new column if it doesn't exist
                String insertColumnQuery = "INSERT INTO rawdata.dbo.lineage_data_db_table_columns (db_table_id, db_column_name,process_name,created_by) VALUES (?, ?,?,?)";
                jdbcTemplate.update(insertColumnQuery, dbTableId, column.columnName(),column.processName(),CREATED_BY);

                // Fetch the newly inserted db_column_id
                dbColumnId = jdbcTemplate.queryForObject(findColumnQuery, String.class, dbTableId, column.columnName(),column.processName(),CREATED_BY);
            } else {
                // Update the existing column
                String updateColumnQuery = "UPDATE rawdata.dbo.lineage_data_db_table_columns SET db_column_name = ? WHERE db_column_id = ?";
                jdbcTemplate.update(updateColumnQuery, column.columnName(), dbColumnId);
            }

            // Step 3: For each file column, insert or update data in lineage_data_file_columns
            for (FileColumn fileColumn : column.fileColumns()) {
                logger.info("Saving file column: {}", fileColumn.columnName());

                // Check if the file column already exists
                String findFileColumnQuery = "SELECT file_column_id FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ? AND file_column_name = ?";
                String fileColumnId = null;
                try{
                    fileColumnId = jdbcTemplate.queryForObject(findFileColumnQuery, String.class, dbColumnId, fileColumn.columnName());
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                if (fileColumnId == null) {
                    // Insert new file column
                    String insertFileColumnQuery = "INSERT INTO rawdata.dbo.lineage_data_file_columns (db_column_id, file_column_name, file_name, file_source,created_by) VALUES (?, ?, ?, ?,?)";
                    jdbcTemplate.update(insertFileColumnQuery, dbColumnId, fileColumn.columnName(), fileColumn.fileName(), fileColumn.fileSource(),CREATED_BY);
                } else {
                    // Update the existing file column
                    String updateFileColumnQuery = "UPDATE rawdata.dbo.lineage_data_file_columns SET file_column_name=?,file_name = ?, file_source = ? WHERE file_column_id = ?";
                    jdbcTemplate.update(updateFileColumnQuery,fileColumn.columnName(), fileColumn.fileName(), fileColumn.fileSource(), fileColumnId);
                }
            }
        }
    }
