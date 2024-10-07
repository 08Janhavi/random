import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const AddEditDataScreen = () => {
    const location = useLocation();
    const navigate = useNavigate();

    // Extract db_column_name, row, etc. from location.state or set empty defaults
    const { databaseName, dbTableName,processName, db_column_name = '', row = [] } = location.state || {};

    // Initialize formData with the correct structure
    const [formData, setFormData] = useState([
        {
            db_column_name: db_column_name || '',
            file_columns: [{ file_column_name: row?.file_column_name || '', file_name: row?.file_name || '', file_source: row?.file_source || '' }],
        }
    ]);

    // Load data into formData when the component mounts or when location state changes
    useEffect(() => {
        if (location.state) {
            setFormData([{
                db_column_name: db_column_name || '',
                file_columns: [{ file_column_name: row?.file_column_name || '', file_name: row?.file_name || '', file_source: row?.file_source || '' }],
            }]);
        }
    }, [location.state, db_column_name, row]);

    // Function to handle form input changes
    const handleInputChange = (dbIndex, fileIndex, e) => {
        const { name, value } = e.target;
        const updatedFormData = [...formData];
        updatedFormData[dbIndex].file_columns[fileIndex] = {
            ...updatedFormData[dbIndex].file_columns[fileIndex],
            [name]: value,
        };
        setFormData(updatedFormData);
    };

    // Function to handle form submission
    const handleSubmit = () => {

        const payload = { databaseName,
            tableName: dbTableName,
            tableColumns: {columnName:formData[0].db_column_name,
                processName:processName,
                fileColumns:formData[0].file_columns[0]
            },
        };
        console.log("payload",payload);
        fetch("http://localhost:8080/saveColumnMappings", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload),
        })
            .then((response) => response.json())
            .then((result) => {
                console.log('Data updated successfully:', result);
            })
            .catch((error) => console.error('Error updating data:', error));
        console.log("hi",JSON.stringify(formData));
    };

    // Function to handle adding a new file column to the existing DB column
    const handleAddFileColumn = (dbIndex) => {
        const updatedFormData = [...formData];
        updatedFormData[dbIndex].file_columns.push({
            file_column_name: '',
            file_name: '',
            file_source: ''
        });
        setFormData(updatedFormData);
    };

    // Function to handle adding a new DB column with empty file columns
    const handleAddDbColumn = () => {
        setFormData((prevFormData) => [
            ...prevFormData,
            {
                db_column_name: `DB_${prevFormData.length + 1}`,
                file_columns: [{ file_column_name: '', file_name: '', file_source: '' }]
            }
        ]);
    };

    // Function to handle deleting a row (either DB or File column)
    const handleDeleteFileColumn = (dbIndex, fileIndex) => {
        const updatedFormData = [...formData];
        updatedFormData[dbIndex].file_columns.splice(fileIndex, 1);  // Remove the selected file column
        setFormData(updatedFormData);
    };

    // Function to delete a DB column and all its file columns
    const handleDeleteDbColumn = (dbIndex) => {
        const updatedFormData = [...formData];
        updatedFormData.splice(dbIndex, 1);  // Remove the selected DB column
        setFormData(updatedFormData);
    };

    // Handle cancel action
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

                                                {dbRow.file_columns.map((fileRow, fileIndex) => (
                                                    <tr key={fileIndex}>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                name="db_column_name"
                                                                value={dbRow.db_column_name}
                                                                disabled // Disable editing of db_column_name
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                name="file_column_name"
                                                                value={fileRow.file_column_name}
                                                                onChange={(e) => handleInputChange(dbIndex, fileIndex, e)}
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                name="file_name"
                                                                value={fileRow.file_name}
                                                                onChange={(e) => handleInputChange(dbIndex, fileIndex, e)}
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                name="file_source"
                                                                value={fileRow.file_source}
                                                                onChange={(e) => handleInputChange(dbIndex, fileIndex, e)}
                                                            />
                                                        </td>
                                                        <td>
                                                            <button onClick={() => handleDeleteFileColumn(dbIndex, fileIndex)}>
                                                                Delete File Column
                                                            </button>
                                                        </td>
                                                    </tr>
                                                ))}
                                                <tr>
                                                    <td colSpan="5">
                                                        <button onClick={() => handleAddFileColumn(dbIndex)} className="add-file-btn">
                                                            Add File Column to {dbRow.db_column_name}
                                                        </button>
                                                    </td>
                                                </tr>
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

        logger.info("Saving lineage data for table: " + table.tableName());
        lineageDataDAO.saveLineageData(table);

        return ResponseEntity.ok("Data saved successfully");
    }
}






@Repository
public class IMLineageDataDAO {

    private static final Logger logger = LogManager.getLogger(IMLineageDataDAO.class.getName());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Existing method for fetching lineage data
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

    // New method for saving lineage data
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

    // Existing method for mapping data from the DB
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
