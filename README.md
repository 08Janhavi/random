import React, { useState, useEffect } from "react";

const TableComponent = () => {
  const [columnNames, setColumnNames] = useState([]);
  const [data, setData] = useState([]); // Placeholder for table data

  useEffect(() => {
    // Simulate fetching column names from the backend
    fetch("/api/getColumnNames")
      .then((response) => response.json())
      .then((data) => {
        setColumnNames(data.colNames); // Assuming backend sends { colNames: ["Code", "Description"] }
      })
      .catch((error) => console.error("Error fetching column names:", error));

    // Simulate fetching table data
    fetch("/api/getTableData")
      .then((response) => response.json())
      .then((data) => {
        setData(data); // Assuming data is an array of objects
      })
      .catch((error) => console.error("Error fetching table data:", error));
  }, []);

  return (
    <div>
      <table border="1" style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr>
            {columnNames.map((col, index) => (
              <th key={index} style={{ padding: "10px", textAlign: "left" }}>
                {col}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((row, rowIndex) => (
            <tr key={rowIndex}>
              {columnNames.map((col, colIndex) => (
                <td key={colIndex} style={{ padding: "10px" }}>
                  {row[col]} {/* Dynamically accessing row values by column name */}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TableComponent;









@RestController
@RequestMapping("/api")
public class TableController {

    @GetMapping("/getColumnNames")
    public Map<String, List<String>> getColumnNames() {
        return Map.of("colNames", List.of("Code", "Description"));
    }

    @GetMapping("/getTableData")
    public List<Map<String, String>> getTableData() {
        return List.of(
            Map.of("Code", "001", "Description", "Item A"),
            Map.of("Code", "002", "Description", "Item B"),
            Map.of("Code", "003", "Description", "Item C")
        );
    }
}
