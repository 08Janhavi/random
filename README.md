@RestController
@RequestMapping
@CrossOrigin(origins="http://localhost:5173")
public class LineageDataController {

    @Autowired
    private IMLineageDataDAO lineageDataDAO;

    private static final Logger logger = LogManager.getLogger(LineageDataController.class.getName());

    @GetMapping("/getDatabases")
    public List<String> getDatabases(){
        try{
            List<String> databases=lineageDataDAO.getAllDatabases();
            return databases;
        }
        catch(Exception e) {
            logger.error("Error fetching databases",e);
            return new ArrayList<>();
        }
    }

    @GetMapping("/getTables")
    public List<String> getTables(){
        try{
            List<String> tables=lineageDataDAO.getAllTables();
            return tables;
        }
        catch(Exception e) {
            logger.error("Error fetching databases",e);
            return new ArrayList<>();
        }
    }


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







@Repository
public class IMLineageDataDAO {
    private static final Logger logger = LogManager.getLogger(IMLineageDataDAO.class.getName());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    String CREATED_BY="janhavi";

    public List<String> getAllDatabases() throws Exception{
        String query="SELECT DISTINCT database_name FROM rawdata.dbo.lineage_data_db_tables";
        return jdbcTemplate.queryForList(query,String.class);
    }

    public List<String> getAllTables() throws Exception{
        String query="SELECT DISTINCT db_table_name FROM rawdata.dbo.lineage_data_db_tables";
        return jdbcTemplate.queryForList(query,String.class);
    }

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

            // Step 4: Check if there are any remaining file columns for the current db_column_id
            String checkFileColumnsQuery = "SELECT COUNT(*) FROM rawdata.dbo.lineage_data_file_columns WHERE db_column_id = ?";
            Integer remainingFileColumns = jdbcTemplate.queryForObject(checkFileColumnsQuery, Integer.class, dbColumnId);

            if (remainingFileColumns != null && remainingFileColumns == 0) {
                // If no file columns are left, delete the db column
                String deleteDbColumnQuery = "DELETE FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_column_id = ?";
                int columnRowsAffected = jdbcTemplate.update(deleteDbColumnQuery, dbColumnId);

                if (columnRowsAffected == 0) {
                    throw new Exception("Failed to delete db column: " + tableColumn.columnName());
                }
            } else {
                logger.info("Skipping db column deletion for column: {} as it still has associated file columns.", tableColumn.columnName());
            }
        }

        // Step 5: After deleting all columns, check if there are any remaining columns for the db_table_id
//        String checkDbColumnsQuery = "SELECT COUNT(*) FROM rawdata.dbo.lineage_data_db_table_columns WHERE db_table_id = ?";
//        Integer remainingDbColumns = jdbcTemplate.queryForObject(checkDbColumnsQuery, Integer.class, dbTableId);
//
//        if (remainingDbColumns != null && remainingDbColumns == 0) {
//            // If no db columns are left, delete the table itself
//            String deleteDbTableQuery = "DELETE FROM rawdata.dbo.lineage_data_db_tables WHERE db_table_id = ?";
//            int tableRowsAffected = jdbcTemplate.update(deleteDbTableQuery, dbTableId);
//
//            if (tableRowsAffected == 0) {
//                throw new Exception("Failed to delete table: " + table.tableName());
//            }
//
//            logger.info("Table {} deleted successfully.", table.tableName());
//        } else {
//            logger.info("Skipping table deletion for table: {} as it still has associated columns.", table.tableName());
//        }
//
//        logger.info("File columns, db columns (where applicable), and table deletion process completed.");
    }


}


public class GenerateService implements IMLineageGeneratorService{
    @Autowired
    private IMLineageDataDAO imLineageDao;
    private static final Logger logger = LogManager.getLogger(GenerateService.class.getName());

    @Override
    public void generateLineageData(String databaseName,String tableName) {
        logger.info("Starting to generate and publish lineage data");
        List<Table> lineageData = imLineageDao.getLineageDataFromDB(databaseName,tableName);
    }
}



public interface IMLineageGeneratorService {
    public void generateLineageData(String databaseName,String tableName);
}





public record FileColumn(String columnName, String fileName, String fileSource) {
}


public record Table(String databaseName,
                    String tableName,
                    List<TableColumn> tableColumns) {
}



public record TableColumn(String columnName,
                          String processName,
                          List<FileColumn> fileColumns) {
}


@SpringBootApplication
@PropertySource("classpath:uat.properties")
public class IMDataLineageApplication {

	public static void main(String[] args) {
		SpringApplication.run(IMDataLineageApplication.class, args);
	}

}



import {BrowserRouter as Router,Route,Routes} from 'react-router-dom';
import ViewDataScreen from './components/ViewDataScreen'
import AddEditDataScreen from './components/AddEditDataScreen'
import "./App.css"
import Header from './components/Header.jsx';

function App(){
  return (
    <>
    <Header/>
    <Router>
      <Routes>
        <Route path="/" element={<ViewDataScreen/>}></Route>
        <Route path="/addEditDataScreen" element={<AddEditDataScreen/>}></Route>
      </Routes>
    </Router>
    </>
  );
}

export default App;





import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const AddEditDataScreen = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const { databaseName, dbTableName, processName, data } = location.state || {};

    const [formData, setFormData] = useState(data || []);

    useEffect(() => {
        if (location.state) {
            setFormData(data || []);
        }
    }, [location.state, data]);

    const handleInputChange = (dbIndex, fileIndex, e) => {
        const { name, value } = e.target;
        const updatedFormData = [...formData];
        
        if (fileIndex === null) {
            updatedFormData[dbIndex] = {
                ...updatedFormData[dbIndex],
                [name]: value,
            };
        } else {
            updatedFormData[dbIndex].rows[fileIndex] = {
                ...updatedFormData[dbIndex].rows[fileIndex],
                [name]: value,
            };
        }
        setFormData(updatedFormData);
    };

    const handleSubmit = () => {
        
        for (let dbRow of formData) {
            for (let fileColumn of dbRow.rows) {
                if (!fileColumn.file_column_name || !fileColumn.file_name || !fileColumn.file_source) {
                    alert("File column name, file name, and file source cannot be empty");
                    return;
                }
            }
        }

        const payload = {
            databaseName,
            tableName: dbTableName,
            tableColumns: formData.map((dbRow) => ({
                columnName: dbRow.db_column_name,
                processName: processName,
                fileColumns: dbRow.rows.map((fileColumn) => ({
                    columnName: fileColumn.file_column_name,
                    fileName: fileColumn.file_name,
                    fileSource: fileColumn.file_source,
                })),
            })),
        };
        console.log("payload", payload);
        fetch("http://localhost:8080/saveColumnMappings", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload),
        })
        .then((response) => response.text())
        .then((result) => {
            console.log('Data updated successfully:', result);
            navigate(-1); // Navigate to the previous page
        })
        .catch((error) => {
            console.error('Error updating data:', error);
        });
    };

    const handleAddFileColumn = (dbIndex) => {
        const updatedFormData = [...formData];
        updatedFormData[dbIndex].rows.push({
            file_column_name: '',
            file_name: '',
            file_source: ''
        });
        setFormData(updatedFormData);
    };

    const handleAddDbColumn = () => {
        setFormData((prevFormData) => [
            ...prevFormData,
            {
                db_column_name: '',
                rows: [{ file_column_name: '', file_name: '', file_source: '' }]
            }
        ]);
    };

    



    const handleDeleteFileColumn = (dbIndex, fileIndex) => {
        console.log(formData)
        console.log(formData[dbIndex].rows)
        if (!formData[dbIndex]) {
            console.error(`Invalid dbIndex: ${dbIndex}`);
            return;
        }
    
        const fileColumns = formData[dbIndex].rows;
        
        if (!fileColumns || !fileColumns[fileIndex]) {
            console.error(`Invalid fileIndex: ${fileIndex}`);
            return;
        }
    
        const fileColumn = fileColumns[fileIndex];
    
        if (fileColumn.file_column_name || fileColumn.file_name || fileColumn.file_source) {
            const payload = {
                databaseName,
                tableName: dbTableName,
                tableColumns: [{
                    columnName: formData[dbIndex].db_column_name,
                    processName: processName,
                    fileColumns: [{
                        columnName: fileColumn.file_column_name,
                        fileName: fileColumn.file_name,
                        fileSource: fileColumn.file_source,
                    }],
                }],
            };
    
            console.log("Delete payload", payload);
    
            fetch(`http://localhost:8080/deleteFileColumn`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
            })
            .then((response) => {
                if (response.ok) {
                    const updatedFormData = [...formData];
                    updatedFormData[dbIndex].rows.splice(fileIndex, 1);  // Remove the selected file column
    
                    if (updatedFormData[dbIndex].rows.length === 0) {
                        updatedFormData.splice(dbIndex, 1); // Remove the DB column if it has no file columns left
                    }
    
                    setFormData(updatedFormData);
                    console.log('File column deleted successfully.');
                } else {
                    console.error('Failed to delete the file column.');
                }
            })
            .catch((error) => {
                console.error('Error deleting file column:', error);
            });
        } else {
            const updatedFormData = [...formData];
            updatedFormData[dbIndex].rows.splice(fileIndex, 1);  // Remove the selected file column
    
            if (updatedFormData[dbIndex].rows.length === 0) {
                updatedFormData.splice(dbIndex, 1); // Remove the DB column if it has no file columns left
            }
    
            setFormData(updatedFormData);
            console.log('File column deleted successfully.');
        }
    };

    const handleCancel = () => {
        navigate(-1);  // Go back without saving
    };

    return (
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
                                <span className="breadcrumbleftInside">
                                    <b>Add/Edit Data Screen</b>
                                </span>
                            </div>
                            <div className="highlight">
                                <table className="headTable">
                                    <tbody>
                                        <tr>
                                            <th>Db Column Name</th>
                                            <th>File Column Name</th>
                                            <th>File Name</th>
                                            <th>File Source</th>
                                            <th>Actions</th>
                                        </tr>
                                        {formData.map((dbRow, dbIndex) => (
                                            <React.Fragment key={dbIndex}>
                                                {dbRow.rows.length > 0 && (
                                                    <>
                                                        <tr>
                                                            <td>
                                                                <input
                                                                    type="text"
                                                                    name="db_column_name"
                                                                    value={dbRow.db_column_name}
                                                                    onChange={(e) => handleInputChange(dbIndex, null, e)}
                                                                    placeholder="Enter DB Column Name"
                                                                />
                                                            </td>
                                                            <td colSpan="4"></td>
                                                        </tr>
                                                    </>
                                                )}
                                                {dbRow.rows.map((fileRow, fileIndex) => (
                                                    <tr key={fileIndex}>
                                                        <td></td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                name="file_column_name"
                                                                value={fileRow.file_column_name}
                                                                onChange={(e) => handleInputChange(dbIndex, fileIndex, e)}
                                                                placeholder="Enter File Column Name"
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                name="file_name"
                                                                value={fileRow.file_name}
                                                                onChange={(e) => handleInputChange(dbIndex, fileIndex, e)}
                                                                placeholder="Enter File Name"
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                name="file_source"
                                                                value={fileRow.file_source}
                                                                onChange={(e) => handleInputChange(dbIndex, fileIndex, e)}
                                                                placeholder="Enter File Source"
                                                            />
                                                        </td>
                                                        <td>
                                                            <button onClick={() => handleDeleteFileColumn(dbIndex, fileIndex)}>
                                                                Delete File Column
                                                            </button>
                                                        </td>
                                                    </tr>
                                                ))}
                                                {dbRow.rows.length > 0 && (
                                                    <tr>
                                                        <td colSpan="5">
                                                            <button onClick={() => handleAddFileColumn(dbIndex)} className="add-file-btn">
                                                                Add File Column to {dbRow.db_column_name || 'New DB Column'}
                                                            </button>
                                                        </td>
                                                    </tr>
                                                )}
                                            </React.Fragment>
                                        ))}
                                        <tr>
                                            <td colSpan="5">
                                                <button onClick={handleAddDbColumn} className="add-db-btn">
                                                    Add New DB Column
                                                </button>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                                <div>
                                    <button onClick={handleSubmit} className="btn submit-btn">
                                        Submit
                                    </button>
                                    <button onClick={handleCancel} className="btn cancel-btn">
                                        Cancel
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AddEditDataScreen;


import React, { Component } from 'react';
import "./Generic.css";

class Header extends Component {
    render() {
        return (
            <div id="header">
                <div id="masterHeader">
                    <div className="bannerModule">
                        <div className="top right">&nbsp;</div>
                        <div className="top">&nbsp;</div>
                        <div className="content">
                            <div className="right">&nbsp;</div>
                            <div id='logo'>
                                <a href='http://www.nomura.com/'><img src='data:image/gif;base64,R0lGODlhyAAWAPcAAP///wMAAMsgJm9nZNLOzcwkJ25lY/LTxssiJ2hgXhYQDfLRxOalj6ihoDUuLNvY13BoZQQAAN3a2f/9/eakjtZkTPj395KKiPPVyKylpOOWfv359/3z7vns5tluVfnq4vv7+2xjYa2npdNOOs4xK3dvbeu5ps0uKvfj2tlwV+amkAkCAEhBPtrW1fPZzdpzWunn5oqDgM86L2VdW+Dd3JOMimRcWfnu6BoUEoJ6ePDMvl1VU3x0cs42LcwpKM7KyQIAAOy7qemxnhcRDj02NO/t7Nx+ZdDMy0xEQsG9u/LSxRYPDNbS0emumigiIHZubCwmJGJaV/Hv78vGxeu1odPQz8wsKe7Ds9NUPtVeR/PWyjIrKTQuK+CMcx8ZFuvp6Oy+rc8/MdhqUXtzcEI7OWtiYOfl5CUfHd+Fa89AMuCKcUA5NomBf/339BgSEOSdhswnKD84NswtKr+6uZSNi5aOjPnr5WNbWC4nJdt6YeOYgcsjJ6GamfDKuxMNCvPXy+epk5mSkPLPwaagnuLf3tNSPWFZVtRWQNVbRDMtKxELB/be09t3XichH/X09A0GA4R9ekM8OeXj4hQOC21kYry3tuCOdVVNSjgyL9VgSCQeHIiAfqmjobOtq+3Ar7exsKCZl+Ph4PPy8UlCP9ZiStpxWNVdRqSdm+Sgib65t/318eu2o4B3ddt4X/nv6SojIa6opx0XFO3r6iAaGCMdG6qkokY+POSbhPbh1/jm3tx8Y09IRf/8+uamkcXBv0Q9OuirlqKcmqWeneKSedBFNfDLvH52dHlwbldPTC8pJ9dlTWlhX97c2+3BsO7CsdBBMuu3pM88MH11czYvLXVta7Wwrt+JcIF5dtdoT8fCwYd/ffLQwswmJ9pyWdp0W+ajjLCqqA4HBNx7YuGQd9ZhSZ6YluSbg5iRj+SfiOy9q9RZQv/7+FFJR/PWyddmTo6GhI2Fg1JKSLizsbq0s52WlZuUklpSUF5WVAYAANBDNEdAPcjEw9FINtjV1NVcRZCJhyH5BAAAAAAALAAAAADIABYAAAj/AAEIHEiwoMGDCBMqXMiwocOHECNKnEhxoSQRMSCxqUWjosePIENG/NBnFZUgGNoMnDChIK+HLQs+GUCzkkF5Bgx8KjjIVoCfP4mAShgjJyuBVWgayGBwk4EBBgIVLDLmKRMAMGbS3DoAApsWCUUd20rAowABEO2oYMBARQeDzSgw+FDQRDcZZxGMsORCIIdebd8CoEIBkMorchk0AZCLLQMKxQwC/Vml4IyfUQiOmsyZjBSDDyZzAsAC6BILBVdE+ImnYOifpwAc4UzbEMJgk5GYRftwwdmzHmIO9HBW0MAPKX4rF3ACHYANWc4uBmDtrBBVPX6TA4BGuRDJk2c5/yIY4meCgXEmJxhkYDIe1AR5TN5lYTVQYQUVAHVQUALQBgAQQBttyBy0BWdfVHQWRAcUoNw4Bb0gAAIL+HXIbyNQocMwcvx2CwCAACcQBnsIYIQnyjmzThi/pcEBeJONQp55AskHVCoDTTHZDAUdOJkwnOmT3379/RfgZMYwQUAdnE1RkICc8aAgbw41uNx3A0lIoUBG/JbFDQM1Y8VZ3GzTAQkCyJALABOoI0AXpfyWDy8mKNfFQQOWMFB5AZz3hX0BQFAQHZPBMFAStLGCA2fMEKTfT/wR5N9PAEL5Ux0DNTDZJgXZQxstIFC04ENWKkcCBllOWGE7cJx1wh8Fdf/xGykAJCeAOQJpYIUJ2Z2lAQBi/LaHEnjSBoQIAvF5HhuTJUhQEYCOMdBlnCWzA2c5OEqkpEZaGsAFBAEq5UAW+BEAECtM1omoVDZUqnJYuCKQlhXq8VsKBv3RoQBWoODMWZm05AIj3/xWgAt29CqAMggB9ShQRwCgLADsAKXJQVwAFYdAFqQbgBeT0QEyUFtoC2mRlB4JFLgDARoDQRn89Aosk0XCLoMOYjGCcsEBoCWx2Pz2q0GI/PbGBsScpcNzKJjymxgANKGcCg3/NAdnUAAwAI14ACWjQUgA5YRAMQfwyBxAAGVDC79MlsRAD0c60KQBVDoZywCUM5kvBGH/8pOeHv9EyESjVumgB0qM+dudWmKwwTO/mXCQrQKgAYCsbwrk22+AAJBJi/IWG4AUZQO1wxg/LQPAEKYfFAVQkwhExE+7APAKUOEAwCRQd8C97dzdTvaPGZVQ+9MOBL0WgBkST1YG4e0yZGUFALyxXBBdIqBFByf8FplBrfyWBwA6tOoid7+R0AEKigsgTkJAPQAANZyNrLobrRv0+k9+AEAIUHMAAOqAkgELTCYCRRBI3FBWN5X9pAaJmIwiskUQQ/ykQAAIxWQmEaqIFM5dDqIeAHShnDBcSHtn+k0fDhK+s4zPc2cJwgSi8RsjXE45noDfT+QHAJ/Q5jxQAAoL/w4Stp80AgAl+AkOQtWCyUwDAEX8CR8U+DuB0M1uQOFDIO5WEEdM4idDOIMmnMAZcEjkg9ILoUA2ULTlIMBxkDuL5AxCOctV7yzeqNNvwOAKLGBIJVULAA/NsCjOnCceFsMYUNYAgK4F4B4CacEaCtWJySSCij+5JEGYAZRBODAANQDAGYAyhAQOZIsDmgwZzhi9hUxvILjAi3K2FCxfHaSNAvgQAFzQqjD48SxZgIYpfPCbYSgkfgP5wQ8BsInJyKIgUgBUDr6QtgCUBQBEyMFkKAGAQv6kH6IESmsI0sSfMMVb4ALFZJ5AkIwFAB8KWII8WTeZjkAEja5U40DA4P+g32zJXmcpRb4UJ4e+CIRyv0FFHky1iGPukCD1MCRWAGWAgtRgMqIogxEFcooAjCEWQKEFAO6gHgBM8ieKKIgIfgKEyqBTII2YDPMAoMyf2HMgCSjpPVupkFcSRAOzrJAWWsUcLcRqVgQBxnJkcAVZnsUdC0EmQUgKlPMAwEY/sYlAslHSQr5DIJvZggWB8oMqTCYWALjGNgfChJHNQiAvBcBKgXIJgVDrFwaZDVD80EGH4LOn+iQII/xZIQB0CZihAwAYiCmAAixtIK5I2m8YoVTlQCOqDy2IO/s0kJP+pAw12Jo45QqUUPjvJ4/Yx2Tqejug+AIE9PyJAiLhN6D/0AGuXBRIbX8igW6a8yCzA8o5doozAYiQIBz45ZYA4IoLnaUQQgCDBrp3Fj0YhIS/cQYuqCuA8zk0AGApCAwUgZmBgGAzqYxEAsnwE90AwBi0CydQpBADVQIgFY9IZQC4KRC9/gQeh8oNPZQ4HoPgZrQP+WtCNuePg2AATQIwjkA+QJzlnMUKqDiIDn4TBlUAQBmLY0gJBlCCZ+YVAjopCCyIAKgIOABAAtEGTX4gED4YAAI0TsKNITAAGlhgJjyGhEAkUQb8AWUFcZgHQWAwYh47aSA1uPEApMGJnFQjITnoypTh0xAFI8QOvaBAEBAiCAqogC4ESYcY+NGDHkSjMhBqaOhBJtCEx1xBIEowMwPWJBKDXCQjG7lpnwdN6EI/hCQmQQkgDc3oRjv60ZAudEAAADs='
                                    alt='Nomura Now'></img></a>
                            </div>
                        </div>
                        <div className="base">
                            <div className="corners right">&nbsp;</div>
                            <div className="corners">&nbsp;</div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default Header




import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const ViewDataScreen = () => {
    const [databaseName, setDatabaseName] = useState(() => {
        return localStorage.getItem('databaseName') || '';
    });
    const [dbTableName, setDbTableName] = useState(() => {
        return localStorage.getItem('dbTableName') || '';
    });
    const [data, setData] = useState([]);
    const [databases, setDatabases] = useState([]);
    const [dbTables, setDbTables] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        localStorage.setItem('databaseName', databaseName);
    }, [databaseName]);

    useEffect(() => {
        localStorage.setItem('dbTableName', dbTableName);
    }, [dbTableName]);

    useEffect(() => {
        fetch(`http://localhost:8080/getDatabases`)
            .then((response) => response.json())
            .then((data) => {
                setDatabases(data);
            })
            .catch((error) => console.error("error fetching databases", error));
    }, []);

    useEffect(() => {
        fetch(`http://localhost:8080/getTables`)
            .then((response) => response.json())
            .then((data) => {
                setDbTables(data);
            })
            .catch((error) => console.error("error fetching tables", error));
    }, []);

    useEffect(() => {
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

    const [processName, setProcessName] = useState("");

    const structureData = (data) => {
        const result = [];
        const table = data[0];

        if (table && table.tableColumns) {
            table.tableColumns.forEach((column) => {
                const fileColumns = column.fileColumns.map(fileCol => ({
                    file_column_name: fileCol.columnName,
                    file_name: fileCol.fileName,
                    file_source: fileCol.fileSource,
                }));
                setProcessName(column.processName);
                result.push({
                    db_column_name: column.columnName,
                    rows: fileColumns,
                });
            });
        }
        return result;
    };

    // Edit button handler
    const handleEdit = () => {
        navigate('/addEditDataScreen', {
            state: { databaseName, dbTableName, processName, data }
        });
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
                                            {databases.map((db) => (
                                                <option key={db} value={db}>{db}</option>
                                            ))}
                                        </select>
                                    </label>
                                    <label>
                                        DB Table Name:
                                        <select value={dbTableName} onChange={(e) => setDbTableName(e.target.value)}>
                                            <option value="">Select Table</option>
                                            {dbTables.map((table) => (
                                                <option key={table} value={table}>{table}</option>
                                            ))}
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
                                            {data.map((item, index) => (
                                                <React.Fragment key={index}>
                                                    {item.rows.map((row, rowIndex) => (
                                                        <tr key={`${index}-${rowIndex}`}>
                                                            {rowIndex === 0 && (
                                                                <td rowSpan={item.rows.length} className='db-column-cell'>{item.db_column_name}</td>
                                                            )}
                                                            <td>{row.file_column_name}</td>
                                                            <td>{row.file_name}</td>
                                                            <td>{row.file_source}</td>
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
                                                    <button onClick={handleEdit} className='btn edit-btn'>Edit</button>
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
    );
};

export default ViewDataScreen;

