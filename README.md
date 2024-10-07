@Repository
public class IMLineageDataDAO {

    private static final Logger logger = LogManager.getLogger(IMLineageDataDAO.class.getName());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void saveLineageData(Table table) {
        logger.info("Saving lineage data for table: {}", table.tableName());

        // Step 1: Get the db_table_id from lineage_data_db_table
        String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_table WHERE database_name = ? AND db_table_name = ?";
        Integer dbTableId = jdbcTemplate.queryForObject(findTableQuery, Integer.class, table.databaseName(), table.tableName());

        if (dbTableId == null) {
            // Insert the new table if it doesn't exist
            String insertTableQuery = "INSERT INTO rawdata.dbo.lineage_data_db_table (database_name, db_table_name) VALUES (?, ?)";
            jdbcTemplate.update(insertTableQuery, table.databaseName(), table.tableName());

            // Fetch the newly inserted db_table_id
            dbTableId = jdbcTemplate.queryForObject(findTableQuery, Integer.class, table.databaseName(), table.tableName());
        }

        // Step 2: For each table column, insert or update data in lineage_data_db_table_columns
        for (TableColumn column : table.tableColumns()) {
            logger.info("Saving column: {}", column.columnName());

            // Check if the column already exists by fetching db_column_id
            String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";
            Integer dbColumnId = jdbcTemplate.queryForObject(findColumnQuery, Integer.class, dbTableId, column.columnName());

            if (dbColumnId == null) {
                // Insert a new column if it doesn't exist
                String insertColumnQuery = "INSERT INTO rawdata.dbo.lineage_data_db_table_columns (db_table_id, db_column_name, process_name) VALUES (?, ?, ?)";
                jdbcTemplate.update(insertColumnQuery, dbTableId, column.columnName(), column.processName());

                // Fetch the newly inserted db_column_id
                dbColumnId = jdbcTemplate.queryForObject(findColumnQuery, Integer.class, dbTableId, column.columnName());
            } else {
                // Update the existing column
                String updateColumnQuery = "UPDATE rawdata.dbo.lineage_data_db_table_columns SET process_name = ? WHERE db_column_id = ?";
                jdbcTemplate.update(updateColumnQuery, column.processName(), dbColumnId);
            }

            // Step 3: For each file column, insert or update data in lineage_data_file_columns
            for (FileColumn fileColumn : column.fileColumns()) {
                logger.info("Saving file column: {}", fileColumn.columnName());

                // Check if the file column already exists
                String findFileColumnQuery = "SELECT file_column_id FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ? AND file_column_name = ?";
                Integer fileColumnId = jdbcTemplate.queryForObject(findFileColumnQuery, Integer.class, dbColumnId, fileColumn.columnName());

                if (fileColumnId == null) {
                    // Insert new file column
                    String insertFileColumnQuery = "INSERT INTO rawdata.dbo.lineage_data_file_columns (db_column_id, file_column_name, file_name, file_source) VALUES (?, ?, ?, ?)";
                    jdbcTemplate.update(insertFileColumnQuery, dbColumnId, fileColumn.columnName(), fileColumn.fileName(), fileColumn.fileSource());
                } else {
                    // Update the existing file column
                    String updateFileColumnQuery = "UPDATE rawdata.dbo.lineage_data_file_columns SET file_name = ?, file_source = ? WHERE file_column_id = ?";
                    jdbcTemplate.update(updateFileColumnQuery, fileColumn.fileName(), fileColumn.fileSource(), fileColumnId);
                }
            }
        }
    }
}
