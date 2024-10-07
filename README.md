package com.nomura.im.lineage.controller;

import com.nomura.im.lineage.dao.IMLineageDataDAO;
import com.nomura.im.lineage.vo.FileColumn;
import com.nomura.im.lineage.vo.Table;
import com.nomura.im.lineage.vo.TableColumn;
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

    // Updated POST method to save data based on the frontend request structure
    @PostMapping("/saveColumnMappings")
    public ResponseEntity<String> saveColumnMappings(@RequestBody SaveColumnMappingsRequest request) {
        if (request == null || request.getTableName() == null || request.getTableName().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid data");
        }

        String tableName = request.getTableName();
        List<TableColumn> columns = request.getColumns();

        if (columns == null || columns.isEmpty()) {
            return ResponseEntity.badRequest().body("No columns provided");
        }

        logger.info("Saving lineage data for table: {}", tableName);

        // Assuming that the database name is known or can be set here; otherwise, modify as needed.
        String databaseName = "your_database_name"; // Replace with actual database name if needed
        Table table = new Table(databaseName, tableName, columns);

        lineageDataDAO.saveLineageData(table);

        return ResponseEntity.ok("Data saved successfully");
    }
}

// New Request class for the incoming request payload
class SaveColumnMappingsRequest {
    private String tableName;
    private List<TableColumn> columns;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<TableColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<TableColumn> columns) {
        this.columns = columns;
    }
}
