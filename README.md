import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const AddEditDataScreen = () => {
    const { state } = useLocation();
    const navigate = useNavigate();
    
    const [data, setData] = useState({});
    const databaseName = state.databaseName;
    const dbTableName = state.dbTableName;

    useEffect(() => {
        // Fetch data from the server based on the database and table name
        if (databaseName && dbTableName) {
            fetch(`http://localhost:8080/getDataForEdit?db=${databaseName}&table=${dbTableName}`)
                .then((response) => response.json())
                .then((fetchedData) => {
                    const groupedData = groupByDbColumnName(fetchedData);
                    setData(groupedData);
                })
                .catch((error) => console.error('Error fetching data:', error));
        }
    }, [databaseName, dbTableName]);

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
                                            <th>Actions</th>
                                        </tr>
                                        {Object.entries(data).map(([dbColumnName, rows], dbIndex) => (
                                            <React.Fragment key={dbIndex}>
                                                {rows.map((row, rowIndex) => (
                                                    <tr key={rowIndex}>
                                                        {rowIndex === 0 && (
                                                            <td rowSpan={rows.length} className='db-column-cell'>{dbColumnName}</td>
                                                        )}
                                                        <td>
                                                            <input
                                                                type="text"
                                                                value={row.fileColumnName}
                                                                onChange={(e) => {
                                                                    const newData = { ...data };
                                                                    newData[dbColumnName][rowIndex].fileColumnName = e.target.value;
                                                                    setData(newData);
                                                                }}
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                value={row.fileName}
                                                                onChange={(e) => {
                                                                    const newData = { ...data };
                                                                    newData[dbColumnName][rowIndex].fileName = e.target.value;
                                                                    setData(newData);
                                                                }}
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                value={row.fileSource}
                                                                onChange={(e) => {
                                                                    const newData = { ...data };
                                                                    newData[dbColumnName][rowIndex].fileSource = e.target.value;
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
