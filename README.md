@Repository
public class IMLineageDataDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LogManager.getLogger(IMLineageDataDAO.class.getName());

    public List<Table> getLineageDataFromDB(String databaseName, String tableName) {
        String query = String.format("SELECT ... FROM ... WHERE database_name = ? AND table_name = ?", databaseName, tableName);
        logger.info("Executing query to retrieve lineage data: {}", query);

        LineageDataRowMapper rowMapper = new LineageDataRowMapper();
        jdbcTemplate.query(query, rowMapper);
        return rowMapper.getLineageData();
    }

    // Method to save or update data
    public void saveOrUpdateLineageData(List<Table> tables) {
        for (Table table : tables) {
            // Check if table exists, if not, insert
            String checkTableQuery = "SELECT COUNT(*) FROM lineage_table WHERE database_name = ? AND table_name = ?";
            int tableCount = jdbcTemplate.queryForObject(checkTableQuery, Integer.class, table.databaseName(), table.tableName());

            if (tableCount == 0) {
                // Insert new table
                String insertTableQuery = "INSERT INTO lineage_table (database_name, table_name) VALUES (?, ?)";
                jdbcTemplate.update(insertTableQuery, table.databaseName(), table.tableName());
            }

            for (TableColumn column : table.tableColumns()) {
                // Insert or update columns
                String checkColumnQuery = "SELECT COUNT(*) FROM lineage_column WHERE db_column_name = ? AND db_table_name = ?";
                int columnCount = jdbcTemplate.queryForObject(checkColumnQuery, Integer.class, column.columnName(), table.tableName());

                if (columnCount == 0) {
                    // Insert new column
                    String insertColumnQuery = "INSERT INTO lineage_column (db_column_name, process_name, db_table_name) VALUES (?, ?, ?)";
                    jdbcTemplate.update(insertColumnQuery, column.columnName(), column.processName(), table.tableName());
                } else {
                    // Update existing column
                    String updateColumnQuery = "UPDATE lineage_column SET process_name = ? WHERE db_column_name = ? AND db_table_name = ?";
                    jdbcTemplate.update(updateColumnQuery, column.processName(), column.columnName(), table.tableName());
                }

                // Handle file columns
                for (FileColumn fileColumn : column.fileColumns()) {
                    String checkFileColumnQuery = "SELECT COUNT(*) FROM file_column WHERE file_column_name = ? AND db_column_name = ?";
                    int fileColumnCount = jdbcTemplate.queryForObject(checkFileColumnQuery, Integer.class, fileColumn.columnName(), column.columnName());

                    if (fileColumnCount == 0) {
                        // Insert new file column
                        String insertFileColumnQuery = "INSERT INTO file_column (file_column_name, file_name, file_source, db_column_name) VALUES (?, ?, ?, ?)";
                        jdbcTemplate.update(insertFileColumnQuery, fileColumn.columnName(), fileColumn.fileName(), fileColumn.fileSource(), column.columnName());
                    } else {
                        // Update existing file column
                        String updateFileColumnQuery = "UPDATE file_column SET file_name = ?, file_source = ? WHERE file_column_name = ? AND db_column_name = ?";
                        jdbcTemplate.update(updateFileColumnQuery, fileColumn.fileName(), fileColumn.fileSource(), fileColumn.columnName(), column.columnName());
                    }
                }
            }
        }
    }
}
