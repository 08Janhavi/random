@Repository
public class IMLineageDataDAO {

    private static final Logger logger = LogManager.getLogger(IMLineageDataDAO.class.getName());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void saveLineageData(Table table) {
        logger.info("Saving lineage data for table: {}", table.tableName());

        try {
            // Step 1: Get the db_table_id from lineage_data_db_table
            String findTableQuery = "SELECT db_table_id FROM rawdata.dbo.lineage_data_db_table WHERE database_name = ? AND db_table_name = ?";
            String dbTableId = null;
            try {
                dbTableId = jdbcTemplate.queryForObject(findTableQuery, String.class, table.databaseName(), table.tableName());
            } catch (EmptyResultDataAccessException e) {
                logger.info("Table not found, inserting a new table entry.");
                // Insert the new table if it doesn't exist
                String insertTableQuery = "INSERT INTO rawdata.dbo.lineage_data_db_table (database_name, db_table_name) VALUES (?, ?)";
                jdbcTemplate.update(insertTableQuery, table.databaseName(), table.tableName());

                // Fetch the newly inserted db_table_id
                dbTableId = jdbcTemplate.queryForObject(findTableQuery, String.class, table.databaseName(), table.tableName());
            }

            // Step 2: For each table column, insert or update data in lineage_data_db_table_columns
            for (TableColumn column : table.tableColumns()) {
                logger.info("Saving column: {}", column.columnName());

                // Check if the column already exists by fetching db_column_id
                String findColumnQuery = "SELECT db_column_id FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ? AND db_column_name = ?";
                String dbColumnId = null;
                try {
                    dbColumnId = jdbcTemplate.queryForObject(findColumnQuery, String.class, dbTableId, column.columnName());
                } catch (EmptyResultDataAccessException e) {
                    logger.info("Column not found, inserting a new column.");
                    // Insert a new column if it doesn't exist
                    String insertColumnQuery = "INSERT INTO rawdata.dbo.lineage_data_db_table_columns (db_table_id, db_column_name) VALUES (?, ?)";
                    jdbcTemplate.update(insertColumnQuery, dbTableId, column.columnName());

                    // Fetch the newly inserted db_column_id
                    dbColumnId = jdbcTemplate.queryForObject(findColumnQuery, String.class, dbTableId, column.columnName());
                }

                // Step 3: For each file column, insert or update data in lineage_data_file_columns
                for (FileColumn fileColumn : column.fileColumns()) {
                    logger.info("Saving file column: {}", fileColumn.columnName());

                    // Check if the file column already exists
                    String findFileColumnQuery = "SELECT file_column_id FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ? AND file_column_name = ?";
                    String fileColumnId = null;
                    try {
                        fileColumnId = jdbcTemplate.queryForObject(findFileColumnQuery, String.class, dbColumnId, fileColumn.columnName());
                    } catch (EmptyResultDataAccessException e) {
                        logger.info("File column not found, inserting a new file column.");
                        // Insert new file column if it doesn't exist
                        String insertFileColumnQuery = "INSERT INTO rawdata.dbo.lineage_data_file_columns (db_column_id, file_column_name, file_name, file_source) VALUES (?, ?, ?, ?)";
                        jdbcTemplate.update(insertFileColumnQuery, dbColumnId, fileColumn.columnName(), fileColumn.fileName(), fileColumn.fileSource());
                    } catch (Exception ex) {
                        logger.error("Error occurred while saving file column: {}", ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error occurred while saving lineage data: {}", ex.getMessage());
            ex.printStackTrace();
        }
    }
}
