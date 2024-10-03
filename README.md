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

    const handleEdit = () => {
        navigate('/addEditDataScreen', { state: { databaseName, dbTableName } });
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
                                            <option value="lineage_data_db_tables">lineage_data_db_tables</option>
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
    )
}

export default ViewDataScreen;






import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const AddEditDataScreen = () => {
    const location = useLocation();
    const navigate = useNavigate();
    
    const [data, setData] = useState({});
    const {databaseName,dbTableName}=location.state || {};

    useEffect(() => {
        // Fetch data from the server based on the database and table name
        if (databaseName && dbTableName) {
            fetch(`http://localhost:8080/getColumnMappings?db=${databaseName}&table=${dbTableName}&_=${new Date().getTime()}`)
                .then((response) => response.json())
                .then((fetchedData) => {
                    const structuredData = structureData(fetchedData);
                    const groupedData = groupByDbColumnName(structuredData);
                    setData(groupedData);
                    console.log(groupedData);
                })
                .catch((error) => console.error('Error fetching data:', error));
        }
    }, [databaseName, dbTableName]);

    

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
                result.push({
                    db_column_name:column.columnName,
                    rows:fileColumns,
                });
            });
        }

        return result;
    };

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
        const newDbColumnName = `DB${Object.keys(data).length + 1}`;
        const newData = { ...data };
        newData[newDbColumnName] = [{
            dbColumnName: newDbColumnName,
            fileColumnName: '',
            fileName: '',
            fileSource: ''
        }];
        setData(newData);
    };

    const handleSubmit = () => {
        // Prepare data for submission
        const submitData = [];
        for (const dbColumnName in data) {
            data[dbColumnName].forEach(row => {
                submitData.push(row);
            });
        }

        // Send the updated data to the server
        fetch(`http://localhost:8080/updateData`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(submitData),
        })
            .then((response) => response.json())
            .then((result) => {
                console.log('Data updated successfully:', result);
                navigate(-1); // Navigate back after submission
            })
            .catch((error) => console.error('Error updating data:', error));
    };

    const handleCancel = () => {
        navigate(-1); // Go back without saving
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
                                <span className="breadcrumbLeftInside">
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
                                        </tr>
                                        {Object.entries(data).map(([db_column_name, rows], dbIndex) => (
                                            <React.Fragment key={dbIndex}>
                                                {rows.map((row, rowIndex) => (
                                                    <tr key={rowIndex}>
                                                        {
                                                        rowIndex === 0 && (
                                                            <td rowSpan={rows.length} className='db-column-cell'>{row.db_column_name}</td>
                                                        )
                                                        }
                                                        <td>
                                                            <input
                                                                type="text"
                                                                value={row.rows.fileColumnName}
                                                                
                                                                onChange={(e) => {
                                                                    const newData = { ...data };
                                                                    newData[db_column_name][rowIndex].fileColumnName = e.target.value;
                                                                    setData(newData);
                                                                }}
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                value={row.rows.fileName}

                                                                onChange={(e) => {
                                                                    const newData = { ...data };
                                                                    newData[db_column_name][rowIndex].fileName = e.target.value;
                                                                    setData(newData);
                                                                }}
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                value={row.rows.fileSource}
                                                                onChange={(e) => {
                                                                    const newData = { ...data };
                                                                    newData[db_column_name][rowIndex].fileSource = e.target.value;
                                                                    setData(newData);
                                                                }}
                                                            />
                                                        </td>
                                                        <td>
                                                            <button onClick={() => handleDelete(dbColumnName, rowIndex)}>Delete</button>
                                                        </td>
                                                    </tr>
                                                ))}
                                                <tr>
                                                    <td colSpan="5">
                                                        <button onClick={() => handleAddFileColumn(dbColumnName)} className="add-file-btn">
                                                            Add File Column
                                                        </button>
                                                    </td>
                                                </tr>
                                            </React.Fragment>
                                        ))}
                                        <tr>
                                            <td colSpan="5">
                                                <button onClick={handleAddDbColumn} className="add-db-btn">
                                                    Add DB Column
                                                </button>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                                <table className="table-xml">
                                    <tbody>
                                        <tr>
                                            <td>
                                                <button onClick={handleSubmit} className='btn submit-btn'>Submit</button>
                                                <button onClick={handleCancel} className='btn cancel-btn'>Cancel</button>
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
    );
};

export default AddEditDataScreen;
