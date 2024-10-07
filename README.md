@Repository
public class IMLineageDataDAO {

    private static final Logger logger = LogManager.getLogger(IMLineageDataDAO.class.getName());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Fetching lineage data from the DB remains unchanged
    public List<Table> getLineageDataFromDB(String databaseName, String tableName) {
        String query = String.format(
                "SELECT t.db_table_id, t.database_name, t.db_table_name, tc.db_column_id, tc.db_column_name, tc.process_name, " +
                        "fc.file_column_name, fc.file_name, fc.file_source " +
                        "FROM %s.dbo.%s t " +
                        "LEFT JOIN %s.dbo.lineage_data_db_table_columns tc ON t.db_table_id = tc.db_table_id " +
                        "LEFT JOIN %s.dbo.lineage_data_file_columns fc ON tc.db_column_id = fc.db_column_id " +
                        "ORDER BY t.db_table_name, tc.db_column_name", databaseName, tableName, databaseName, databaseName);
        logger.info("Executing query to retrieve lineage data : {}", query);
        LineageDataRowMapper rowMapper = new LineageDataRowMapper();
        jdbcTemplate.query(query, rowMapper);
        return rowMapper.getLineageData();
    }

    // Method to save the lineage data to the DB
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

    // Reverse mapping from the frontend data to Table, TableColumn, and FileColumn objects
    public Table mapToTable(String dbTableName, List<Map<String, String>> fileColumnsData) {
        Map<String, TableColumn> columnMap = new HashMap<>();
        
        // Loop through the data provided by the frontend
        for (Map<String, String> rowData : fileColumnsData) {
            String dbColumnName = rowData.get("db_column_name");
            String fileColumnName = rowData.get("file_column_name");
            String fileName = rowData.get("file_name");
            String fileSource = rowData.get("file_source");
            
            // If the TableColumn doesn't exist yet, create it
            TableColumn tableColumn = columnMap.getOrDefault(dbColumnName, new TableColumn(dbColumnName, "process_name", new ArrayList<>()));
            FileColumn fileColumn = new FileColumn(fileColumnName, fileName, fileSource);
            
            // Add the FileColumn to the TableColumn
            tableColumn.fileColumns().add(fileColumn);
            columnMap.put(dbColumnName, tableColumn);
        }
        
        // Create and return the Table object
        return new Table("database_name", dbTableName, new ArrayList<>(columnMap.values()));
    }

    // Example method to process the incoming frontend data
    public void saveLineageDataFromFrontend(String dbTableName, List<Map<String, String>> fileColumnsData) {
        // Map the flat data structure to the Table object
        Table table = mapToTable(dbTableName, fileColumnsData);

        // Save the mapped data to the DB
        saveLineageData(table);
    }

    // Existing RowMapper remains the same
    private static class LineageDataRowMapper implements RowMapper<Table> {
        private final List<Table> lineageData = new ArrayList<>();
        private Table currentTable;
        private TableColumn currentColumn;

        @Override
        public Table mapRow(ResultSet rs, int rowNum) throws SQLException {
            String tableName = rs.getString("db_table_name");
            logger.debug("Processing table : {}", tableName);

            currentTable = lineageData.stream().filter(t -> t.tableName().equals(tableName)).findFirst().orElse(null);
            if (currentTable == null) {
                logger.info("Creating new table entry for table : {}", tableName);
                currentTable = new Table(rs.getString("database_name"), rs.getString("db_table_name"), new ArrayList<>());
                lineageData.add(currentTable);
            }

            String columnName = rs.getString("db_column_name");
            logger.debug("Processing column : {}", columnName);

            currentColumn = currentTable.tableColumns().stream().filter(c -> c.columnName().equals(columnName)).findFirst().orElse(null);
            if (currentColumn == null) {
                logger.info("Creating new column entry for column : {}", columnName);
                currentColumn = new TableColumn(rs.getString("db_column_name"), rs.getString("process_name"), new ArrayList<>());
                currentTable.tableColumns().add(currentColumn);
            }

            if (rs.getString("file_column_name") != null) {
                String fileColumnName = rs.getString("file_column_name");
                logger.debug("Processing file column : {}", fileColumnName);
                FileColumn fileColumn = new FileColumn(rs.getString("file_column_name"), rs.getString("file_name"), rs.getString("file_source"));
                currentColumn.fileColumns().add(fileColumn);
            }

            return null;
        }

        public List<Table> getLineageData() {
            return lineageData;
        }
    }
}
