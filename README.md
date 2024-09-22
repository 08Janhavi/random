import React, { useEffect, useState } from 'react';
import './App.css';

const App = () => {
  const [data, setData] = useState({});

  useEffect(() => {
    // Replace with your API endpoint
    fetch('/api/getData')
      .then((response) => response.json())
      .then((data) => {
        const groupedData = groupByDbColumnName(data);
        setData(groupedData);
      })
      .catch((error) => console.error('Error fetching data:', error));
  }, []);

  const groupByDbColumnName = (data) => {
    const grouped = {};
    data.forEach((row) => {
      if (!grouped[row.dbColumnName]) {
        grouped[row.dbColumnName] = [];
      }
      grouped[row.dbColumnName].push(row);
    });
    return grouped;
  };

  const handleDelete = (dbColumnName, index) => {
    const newData = { ...data };
    newData[dbColumnName].splice(index, 1);

    // If no more file column rows exist, delete the whole entry
    if (newData[dbColumnName].length === 0) {
      delete newData[dbColumnName];
    }

    setData(newData);
  };

  const handleAddFileColumn = (dbColumnName) => {
    const newData = { ...data };
    newData[dbColumnName].push({
      dbColumnName,
      fileColumnName: '',
      fileName: '',
      fileSource: ''
    });
    setData(newData);
  };

  const handleAddDbColumn = () => {
    const newDbColumnName = `DB${Object.keys(data).length + 1}`; // Create new db column name (this can be changed)
    const newData = { ...data };

    newData[newDbColumnName] = [
      {
        dbColumnName: newDbColumnName,
        fileColumnName: '',
        fileName: '',
        fileSource: ''
      }
    ];

    setData(newData);
  };

  return (
    <div className="App">
      <h1>Database File Details</h1>
      <table>
        <thead>
          <tr>
            <th>Db Column Name</th>
            <th>File Column Name</th>
            <th>File Name</th>
            <th>File Source</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {Object.entries(data).map(([dbColumnName, rows], dbIndex) => (
            <React.Fragment key={dbIndex}>
              {rows.map((row, rowIndex) => (
                <tr key={rowIndex}>
                  {rowIndex === 0 && (
                    <td rowSpan={rows.length}>{dbColumnName}</td>
                  )}
                  <td>{row.fileColumnName}</td>
                  <td>{row.fileName}</td>
                  <td>{row.fileSource}</td>
                  <td>
                    <button onClick={() => handleDelete(dbColumnName, rowIndex)}>
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
              {/* Add File Column button after each set of file column details */}
              <tr>
                <td colSpan="5">
                  <button
                    onClick={() => handleAddFileColumn(dbColumnName)}
                    className="add-file-btn"
                  >
                    Add File Column
                  </button>
                </td>
              </tr>
            </React.Fragment>
          ))}
          {/* Add New DB Column button at the end of all rows */}
          <tr>
            <td colSpan="5">
              <button onClick={handleAddDbColumn} className="add-db-btn">
                Add DB Column
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  );
};

export default App;
