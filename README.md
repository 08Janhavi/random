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



import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const AddEditDataScreen = () => {
    const location = useLocation();
    const navigate = useNavigate();

    // Extract db_column_name, row, etc. from location.state or set empty defaults
    const { databaseName, dbTableName, processName, db_column_name = '', row = [] } = location.state || {};

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
        if (fileIndex === null) {
            updatedFormData[dbIndex] = {
                ...updatedFormData[dbIndex],
                [name]: value,
            };
        } else {
            updatedFormData[dbIndex].file_columns[fileIndex] = {
                ...updatedFormData[dbIndex].file_columns[fileIndex],
                [name]: value,
            };
        }
        setFormData(updatedFormData);
    };

    // Function to handle form submission
    const handleSubmit = () => {
        for (let dbRow of formData) {
            for (let fileColumn of dbRow.file_columns) {
                if (!fileColumn.file_column_name || !fileColumn.file_name || !fileColumn.file_source) {
                    alert("file column name, file name and file source cannot be empty");
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
                fileColumns: dbRow.file_columns.map((fileColumn) => ({
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
                db_column_name: '',
                file_columns: [{ file_column_name: '', file_name: '', file_source: '' }]
            }
        ]);
    };

    // Function to handle deleting a row (either DB or File column)
    const handleDeleteFileColumn = (dbIndex, fileIndex) => {
        const fileColumn=formData[dbIndex].file_columns[fileIndex];
    // Construct the payload similar to handleSubmit
    if(fileColumn.file_column_name || fileColumn.file_name || fileColumn.file_source){
        const payload = {
            databaseName,
            tableName: dbTableName,
            tableColumns: formData.map((dbRow) => ({
                columnName: dbRow.db_column_name,
                processName: processName,
                fileColumns: dbRow.file_columns
                    .map((fileColumn ,i)=> ({
                        columnName: fileColumn.file_column_name,
                        fileName: fileColumn.file_name,
                        fileSource: fileColumn.file_source,
                    })),
            })),
        };

        console.log("Delete payload", payload);

    // Send a DELETE request with the updated payload
    fetch(`http://localhost:8080/deleteFileColumn`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
    })
    .then(response => {
        if (response.ok) {
            // Update the frontend only if the deletion was successful
            const updatedFormData = [...formData];
            updatedFormData[dbIndex].file_columns.splice(fileIndex, 1);  // Remove the selected file column
            if (updatedFormData[dbIndex].file_columns.length === 0) {
                updatedFormData.splice(dbIndex, 1); // Remove the DB column if it has no file columns left
            }
    
            setFormData(updatedFormData);
            console.log('File column deleted successfully.');
            navigate(-1);
        } else {
            console.error('Failed to delete the file column.');
        }
    })
    .catch(error => {
        console.error('Error deleting file column:', error);
    });
    }
    else{
        const updatedFormData = [...formData];
            updatedFormData[dbIndex].file_columns.splice(fileIndex, 1);  // Remove the selected file column
            if (updatedFormData[dbIndex].file_columns.length === 0) {
                updatedFormData.splice(dbIndex, 1); // Remove the DB column if it has no file columns left
            }
    
            setFormData(updatedFormData);
            console.log('File column deleted successfully.');
    }

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
                                                {dbRow.file_columns.length > 0 && (
                                                    <>
                                                        {/* Render DB column name only once */}
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
                                                {dbRow.file_columns.map((fileRow, fileIndex) => (
                                                    <tr key={fileIndex}>
                                                        <td></td> {/* Keep DB column name in its row */}
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
                                                {dbRow.file_columns.length > 0 && (
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

