import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const AddEditDataScreen = () => {
    const location = useLocation();
    const navigate = useNavigate();

    // Extract db_column_name and row from location.state
    const { db_column_name = '', row = [] } = location.state || {};

    const [data, setData] = useState({});  // Initialize data as an empty object

    // Load data into the state if available (Ensure all fields are populated)
    useEffect(() => {
        if (location.state) {
            setData((prevData) => ({
                ...prevData,
                [db_column_name]: row.length
                    ? row // If rows exist, use them
                    : [{
                        db_column_name,
                        file_column_name: '',
                        file_name: '',
                        file_source: '',
                    }],
            }));
        }
    }, [location.state, db_column_name, row]);

    // Handle deletion of a row under a specific dbColumnName
    const handleDelete = (dbColumnName, index) => {
        const newData = { ...data };
        newData[dbColumnName].splice(index, 1);  // Remove the row at the given index
        if (newData[dbColumnName].length === 0) {
            delete newData[dbColumnName];  // Delete the key if no rows left
        }
        setData(newData);  // Update state
    };

    // Add a blank file column under a specific db_column_name
    const handleAddFileColumn = (dbColumnName) => {
        const newData = { ...data };
        if (!newData[dbColumnName]) {
            newData[dbColumnName] = [];
        }
        const newRow = {
            db_column_name: dbColumnName,
            file_column_name: '',
            file_name: '',
            file_source: '',
        };
        newData[dbColumnName].push(newRow);
        setData(newData);  // Update state
    };

    // Add a new DB column with empty file columns
    const handleAddDbColumn = () => {
        const newDbColumnName = `DB_${Object.keys(data).length + 1}`;
        const newData = { ...data };
        newData[newDbColumnName] = [{
            db_column_name: newDbColumnName,
            file_column_name: '',
            file_name: '',
            file_source: '',
        }];
        setData(newData);  // Update state
    };

    // Handle form submission
    const handleSubmit = () => {
        const submitData = [];
        for (const dbColumnName in data) {
            submitData.push(...data[dbColumnName]);  // Collect all rows
        }
        fetch("http://localhost:8080/updateData", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(submitData),
        })
            .then((response) => response.json())
            .then((result) => {
                console.log('Data updated successfully:', result);
                navigate(-1);  // Go back after submission
            })
            .catch((error) => console.error('Error updating data:', error));
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
                                        {Object.entries(data).map(([db_column_name, rows], dbIndex) => (
                                            <React.Fragment key={dbIndex}>
                                                {rows.map((row, rowIndex) => (
                                                    <tr key={rowIndex}>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                value={row.db_column_name}
                                                                disabled // Disable editing of db_column_name
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                value={row.file_column_name}
                                                                onChange={(e) => {
                                                                    const updatedRows = [...rows];
                                                                    updatedRows[rowIndex] = {
                                                                        ...row,
                                                                        file_column_name: e.target.value,
                                                                    };
                                                                    setData({
                                                                        ...data,
                                                                        [db_column_name]: updatedRows,
                                                                    });
                                                                }}
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                value={row.file_name}
                                                                onChange={(e) => {
                                                                    const updatedRows = [...rows];
                                                                    updatedRows[rowIndex] = {
                                                                        ...row,
                                                                        file_name: e.target.value,
                                                                    };
                                                                    setData({
                                                                        ...data,
                                                                        [db_column_name]: updatedRows,
                                                                    });
                                                                }}
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                value={row.file_source}
                                                                onChange={(e) => {
                                                                    const updatedRows = [...rows];
                                                                    updatedRows[rowIndex] = {
                                                                        ...row,
                                                                        file_source: e.target.value,
                                                                    };
                                                                    setData({
                                                                        ...data,
                                                                        [db_column_name]: updatedRows,
                                                                    });
                                                                }}
                                                            />
                                                        </td>
                                                        <td>
                                                            <button onClick={() => handleDelete(db_column_name, rowIndex)}>
                                                                Delete
                                                            </button>
                                                        </td>
                                                    </tr>
                                                ))}
                                                <tr>
                                                    <td colSpan="5">
                                                        <button
                                                            onClick={() => handleAddFileColumn(db_column_name)}
                                                            className="add-file-btn"
                                                        >
                                                            Add File Column
                                                        </button>
                                                    </td>
                                                </tr>
                                            </React.Fragment>
                                        ))}
                                        <tr>
                                            <td colSpan="5">
                                                <button
                                                    onClick={handleAddDbColumn}
                                                    className="add-db-btn"
                                                >
                                                    Add DB Column
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
