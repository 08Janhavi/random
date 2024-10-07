package com.nomura.im.lineage.dao;

import com.nomura.im.lineage.dto.FileColumnDTO;
import com.nomura.im.lineage.dto.FrontendDataDTO;
import com.nomura.im.lineage.vo.FileColumn;
import com.nomura.im.lineage.vo.Table;
import com.nomura.im.lineage.vo.TableColumn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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

    // Method to save data from frontend
    public void saveLineageDataFromFrontend(String tableName, List<FrontendDataDTO> frontendData) {
        // Convert frontendData to Table object
        Table table = mapFrontendDataToTable(tableName, frontendData);

        // Save to DB
        saveLineageData(table);
    }

    private Table mapFrontendDataToTable(String tableName, List<FrontendDataDTO> frontendData) {
        String databaseName = "your_database_name"; // Replace with actual value or make it a parameter

        List<TableColumn> tableColumns = new ArrayList<>();
        Map<String, TableColumn> columnMap = new HashMap<>();

        for (FrontendDataDTO frontendColumn : frontendData) {
            String dbColumnName = frontendColumn.getDbColumnName();
            List<FileColumnDTO> fileColumnsDTO = frontendColumn.getFileColumns();

            TableColumn tableColumn = columnMap.get(dbColumnName);
            if (tableColumn == null) {
                // Here, you might want to get processName from frontend or set a default
                tableColumn = new TableColumn(dbColumnName, "process_name", new ArrayList<>()); // Replace "process_name" with actual logic if needed
                columnMap.put(dbColumnName, tableColumn);
                tableColumns.add(tableColumn);
            }

            for (FileColumnDTO fileColumnDTO : fileColumnsDTO) {
                FileColumn fileColumn = new FileColumn(fileColumnDTO.getFileColumnName(), fileColumnDTO.getFileName(), fileColumnDTO.getFileSource());
                tableColumn.fileColumns().add(fileColumn);
            }
        }

        return new Table(databaseName, tableName, tableColumns);
    }

    // Method to save lineage data to the DB
    @Transactional
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

    // Existing RowMapper remains unchanged
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





package com.nomura.im.lineage.controller;

import com.nomura.im.lineage.dao.IMLineageDataDAO;
import com.nomura.im.lineage.dto.SaveLineageDataRequest;
import com.nomura.im.lineage.vo.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/lineage")
@CrossOrigin(origins = "http://localhost:5173")
public class LineageDataController {

    private static final Logger logger = LogManager.getLogger(LineageDataController.class.getName());

    @Autowired
    private IMLineageDataDAO lineageDataDAO;

    // Existing GET method
    @GetMapping("/getColumnMappings")
    public List<Table> getColumnMappings(@RequestParam("db") String db, @RequestParam("table") String table) {
        if (db == null || table == null) return new ArrayList<>();
        logger.info("Fetching lineage data for DB: {} and Table: {}", db, table);
        return lineageDataDAO.getLineageDataFromDB(db, table);
    }

    // Updated POST method to save data
    @PostMapping("/saveColumnMappings")
    public ResponseEntity<String> saveColumnMappings(@RequestBody SaveLineageDataRequest request) {
        if (request == null || request.getTableName() == null || request.getTableName().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid data");
        }

        String tableName = request.getTableName();
        List<com.nomura.im.lineage.dto.FrontendDataDTO> frontendData = request.getColumns();

        if (frontendData == null || frontendData.isEmpty()) {
            return ResponseEntity.badRequest().body("No columns provided");
        }

        logger.info("Saving lineage data for table: {}", tableName);
        lineageDataDAO.saveLineageDataFromFrontend(tableName, frontendData);

        return ResponseEntity.ok("Data saved successfully");
    }
}





import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const AddEditDataScreen = () => {
    const location = useLocation();
    const navigate = useNavigate();

    // Extract db_column_name, rows, etc. from location.state or set empty defaults
    const { db_column_name = '', row = [] } = location.state || {};

    // Initialize formData with the correct structure
    const [formData, setFormData] = useState([
        {
            db_column_name: db_column_name || '',
            file_columns: row.length ? row : [{
                file_column_name: '',
                file_name: '',
                file_source: '',
            }],
        }
    ]);

    // Function to handle form input changes
    const handleInputChange = (index, e) => {
        const { name, value } = e.target;
        const updatedFormData = [...formData];
        updatedFormData[index] = {
            ...updatedFormData[index],
            [name]: value,
        };
        setFormData(updatedFormData);
    };

    // Function to handle adding a new file column (row)
    const handleAddFileColumn = (index) => {
        const updatedFormData = [...formData];
        updatedFormData[index].file_columns.push({
            file_column_name: '',
            file_name: '',
            file_source: '',
        });
        setFormData(updatedFormData);
    };

    // Function to handle adding a new DB column
    const handleAddDbColumn = () => {
        setFormData([...formData, {
            db_column_name: `DB_${formData.length + 1}`,
            file_columns: [{
                file_column_name: '',
                file_name: '',
                file_source: '',
            }],
        }]);
    };

    // Function to handle deletion of a file column
    const handleDeleteFileColumn = (dbIndex, fileIndex) => {
        const updatedFormData = [...formData];
        updatedFormData[dbIndex].file_columns.splice(fileIndex, 1);
        setFormData(updatedFormData);
    };

    // Function to handle deletion of a DB column
    const handleDeleteDbColumn = (dbIndex) => {
        const updatedFormData = [...formData];
        updatedFormData.splice(dbIndex, 1);
        setFormData(updatedFormData);
    };

    // Handle form submission
    const handleSubmit = () => {
        const tableName = "my_table_name"; // Define this dynamically as needed

        const payload = {
            tableName: tableName,
            columns: formData,
        };

        fetch("http://localhost:8080/lineage/saveColumnMappings", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload),
        })
        .then((response) => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then((result) => {
            console.log('Data updated successfully:', result);
            navigate(-1);  // Navigate back after successful submission
        })
        .catch((error) => {
            console.error('Error updating data:', error);
        });
    };

    // Handle cancel action
    const handleCancel = () => {
        navigate(-1);  // Navigate back without saving
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
                                                                onChange={(e) => handleInputChange(dbIndex, e)}
                                                                disabled // Disable editing of db_column_name
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                name="file_column_name"
                                                                value={fileRow.file_column_name}
                                                                onChange={(e) => handleInputChange(dbIndex, e)}
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                name="file_name"
                                                                value={fileRow.file_name}
                                                                onChange={(e) => handleInputChange(dbIndex, e)}
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                name="file_source"
                                                                value={fileRow.file_source}
                                                                onChange={(e) => handleInputChange(dbIndex, e)}
                                                            />
                                                        </td>
                                                        <td>
                                                            <button onClick={() => handleDeleteFileColumn(dbIndex, fileIndex)}>
                                                                Delete
                                                            </button>
                                                        </td>
                                                    </tr>
                                                ))}
                                                <tr>
                                                    <td colSpan="5">
                                                        <button onClick={() => handleAddFileColumn(dbIndex)} className="add-file-btn">
                                                            Add File Column to {dbRow.db_column_name}
                                                        </button>
                                                        <button onClick={() => handleDeleteDbColumn(dbIndex)} className="delete-db-btn">
                                                            Delete {dbRow.db_column_name}
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
