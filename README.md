import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';


const ViewDataScreen = () => {
    const [databaseName, setDatabaseName] = useState(()=>{
        return localStorage.getItem('databaseName') || '';
    });
    const [dbTableName, setDbTableName] = useState(()=>{
        return localStorage.getItem('dbTableName') || '';
    });
    const [data, setData] = useState([]);
    const navigate = useNavigate();

    useEffect(()=>{
        localStorage.setItem('databaseName',databaseName);
    },[databaseName]);

    useEffect(()=>{
        localStorage.setItem('dbTableName',dbTableName);
    },[dbTableName]);

    useEffect(() => {
        console.log(databaseName,dbTableName);
        if (databaseName && dbTableName) {
            fetch(`http://localhost:8080/getColumnMappings?db=${databaseName}&table=${dbTableName}&_=${new Date().getTime()}`)
                .then((response) => response.json())
                .then((data) => {
                    const structuredData = structureData(data);
                    setData(structuredData);
                })
                .catch((error) => console.error('Error fetching data:', error));
        }
    }, [databaseName, dbTableName]);

    const [processName,setProcessName]=useState("");

    const structureData= (data) => {
        const result = [];
        const table = data[0];

        if(table && table.tableColumns){
            table.tableColumns.forEach((column) =>{
                const fileColumns=column.fileColumns.map(fileCol => ({
                    file_column_name:fileCol.columnName,
                    file_name:fileCol.fileName,
                    file_source:fileCol.fileSource,
                }));
                setProcessName(column.processName);
                console.log(column.processName);
                result.push({
                    db_column_name:column.columnName,
                    rows:fileColumns,
                });
            });
        }
        return result;
    };

    // const groupByDbColumnName = (data) => {
    //     const grouped = {};
    //     data.forEach((row) => {
    //         if (!grouped[row.db_column_name]) {
    //             grouped[row.db_column_name] = [];
    //         }
    //         grouped[row.db_column_name].push(row);
    //     });
    //     return grouped;
    // };

    const handleEdit = (db_column_name,row) => {
        navigate('/addEditDataScreen', { state: { databaseName, dbTableName,processName,db_column_name,row } });
    };

    const handleClose = () => {
        window.close();
    };

    return (
        <>
            <div className="root">
                <div className="main">
                    <div id="holder">
                        <div id="content-top">
                            <div id="bannerContentSmall">
                                <div className="header">
                                    <div className="headerLeft"></div>
                                </div>
                            </div>
                        </div>
                        <div className="content-bottom">
                            <div className="top-most-div">
                                <div className="breadcrumb">
                                    <span className="breadcrumbLeftInside">
                                        <b>View Data Screen</b>
                                    </span>

                                </div>
                                <div className="dropdowns-container">
                                    <label>
                                        Database Name:
                                        <select value={databaseName} onChange={(e) => setDatabaseName(e.target.value)}>
                                            <option value="">Select Database</option>
                                            <option value="rawdata">rawdata</option>
                                            <option value="DB2">DB2</option>
                                            <option value="DB3">DB3</option>
                                        </select>
                                    </label>
                                    <label>
                                        DB Table Name:
                                        <select value={dbTableName} onChange={(e) => setDbTableName(e.target.value)}>
                                            <option value="">Select Table</option>
                                            <option value="products">products</option>
                                            <option value="Table2">Table2</option>
                                            <option value="Table3">Table3</option>
                                        </select>
                                    </label>
                                </div>
                                <div className="highlight">
                                    <table className="headTable">
                                        <tbody>
                                            <tr>
                                                <th>DB Column Name</th>
                                                <th>File Column Name</th>
                                                <th>File Name</th>
                                                <th>File Source</th>
                                            </tr>
                                            {data.map((item,index) => (
                                                <React.Fragment key={index}>
                                                    {item.rows.map((row, rowIndex) => (
                                                        <tr key={`${index}-${rowIndex}`}>
                                                            {rowIndex === 0 && (
                                                                <td rowSpan={item.rows.length} className='db-column-cell'>{item.db_column_name}</td>
                                                            )}
                                                            <td>{row.file_column_name}</td>
                                                            <td>{row.file_name}</td>
                                                            <td>{row.file_source}</td>
                                                            <button onClick={()=> handleEdit(item.db_column_name,row)} className='btn edit-btn'>Edit</button>
                                                        </tr>
                                                    ))}
                                                </React.Fragment>
                                            ))}
                                        </tbody>

                                    </table>
                                    <table className="table-xml">
                                        <tbody>
                                            <tr>
                                                <td>
                                                    
                                                    <button onClick={handleClose} className='btn close-btn'>Close</button>
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>


export default ViewDataScreen;






        @Repository
public class IMLineageDataDAO {

    private static final Logger logger = LogManager.getLogger(IMLineageDataDAO.class.getName());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String CREATED_BY="janhavi";

    public List<Table> getLineageDataFromDB(String databaseName, String tableName) {
        String query =
                "SELECT t.db_table_id, t.database_name, t.db_table_name, tc.db_column_id, tc.db_column_name, tc.process_name, " +
                        "fc.file_column_name, fc.file_name, fc.file_source " +
                        "FROM rawdata.dbo.lineage_data_db_tables t " +
                        "LEFT JOIN rawdata.dbo.lineage_data_db_table_columns tc ON t.db_table_id = tc.db_table_id " +
                        "LEFT JOIN rawdata.dbo.lineage_data_file_columns fc ON tc.db_column_id = fc.db_column_id " +
                        "WHERE  t.database_name=? AND t.db_table_name=?" +
                        "ORDER BY t.db_table_name, tc.db_column_name";
        logger.info("Executing query to retrieve lineage data : {}", query);
        LineageDataRowMapper rowMapper = new LineageDataRowMapper();
        jdbcTemplate.query(query, rowMapper,databaseName,tableName);
        return rowMapper.getLineageData();
    }

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
}












@RestController
@RequestMapping
@CrossOrigin(origins="http://localhost:5173")
public class LineageDataController {

    @Autowired
    private IMLineageDataDAO lineageDataDAO;

    private static final Logger logger = LogManager.getLogger(LineageDataController.class.getName());

    @GetMapping("/getColumnMappings")
    public List<Table> getColumnMappings(@RequestParam("db") String db, @RequestParam("table") String table){
        if(db==null || table==null) return new ArrayList<>();
        logger.info("data printed");
        return lineageDataDAO.getLineageDataFromDB(db,table);
    }


    @PostMapping("/saveColumnMappings")
    public ResponseEntity<String> saveColumnMappings(@RequestBody Table table) {
        System.out.println("reached inside");
        logger.info("hey im here");
        if (table == null) return ResponseEntity.badRequest().body("Invalid data");

        logger.info("updating lineage data " );
        lineageDataDAO.saveLineageData(table);

        return ResponseEntity.ok("Data saved successfully");
    }


    @DeleteMapping("/deleteFileColumn")
    public ResponseEntity<String> deleteFileColumn(@RequestBody Table table) {
        logger.info("Received request to delete file column for table: {}", table.tableName());
        System.out.println(table);

        // Check if table or file columns are null
        if (table == null || table.tableColumns() == null || table.tableColumns().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid data");
        }

        try {
            lineageDataDAO.deleteFileColumn(table);
            return ResponseEntity.ok("File column deleted successfully");
        } catch (Exception e) {
            logger.error("Error occurred while deleting file column: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete file column");
        }
    }
}
    )
}


