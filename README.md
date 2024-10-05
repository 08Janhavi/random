import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const AddEditDataScreen = () => {
    const location = useLocation();
    const navigate = useNavigate();

    // Extract db_column_name, rows, etc. from location.state or set empty defaults
    const { db_column_name = '', row = [] } = location.state || {};

    // Initialize formData with state or empty defaults
    const [formData, setFormData] = useState({
        [db_column_name]: row.length ? row : [{
            db_column_name: db_column_name || '',
            file_column_name: '',
            file_name: '',
            file_source: '',
        }]
    });

    // Load initial data if location.state exists
    useEffect(() => {
        if (location.state) {
            setFormData((prevFormData) => ({
                ...prevFormData,
                [db_column_name]: row.length ? row : [{
                    db_column_name,
                    file_column_name: '',
                    file_name: '',
                    file_source: '',
                }],
            }));
        }
    }, [location.state, db_column_name, row]);

    // Function to handle deletion of a row
    const handleDelete = (dbColumnName, index) => {
        const newFormData = { ...formData };
        newFormData[dbColumnName].splice(index, 1); // Remove the selected row
        if (newFormData[dbColumnName].length === 0) {
            delete newFormData[dbColumnName]; // Remove key if no rows left
        }
        setFormData(newFormData); // Update state
    };

    // Function to add a blank file column for the specific DB column
    const handleAddFileColumn = (dbColumnName) => {
        const newFormData = { ...formData };
        const newRow = {
            db_column_name: dbColumnName,
            file_column_name: '',
            file_name: '',
            file_source: ''
        };
        newFormData[dbColumnName].push(newRow); // Add the new blank row
        setFormData(newFormData); // Update state
    };

    // Function to add a new DB column
    const handleAddDbColumn = () => {
        const newDbColumnName = `DB_${Object.keys(formData).length + 1}`;
        const newFormData = { ...formData };
        newFormData[newDbColumnName] = [{
            db_column_name: newDbColumnName,
            file_column_name: '',
            file_name: '',
            file_source: ''
        }];
        setFormData(newFormData); // Update state
    };

    // Handle form submission
    const handleSubmit = () => {
        const submitData = [];
        for (const dbColumnName in formData) {
            submitData.push(...formData[dbColumnName]); // Collect all rows
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
                navigate(-1); // Go back after submission
            })
            .catch((error) => console.error('Error updating data:', error));
    };

    // Handle cancel action
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
                                        {Object.entries(formData).map(([db_column_name, rows], dbIndex) => (
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
                                                                    const newRows = [...formData[db_column_name]];
                                                                    newRows[rowIndex].file_column_name = e.target.value;
                                                                    setFormData({
                                                                        ...formData,
                                                                        [db_column_name]: newRows
                                                                    });
                                                                }}
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                value={row.file_name}
                                                                onChange={(e) => {
                                                                    const newRows = [...formData[db_column_name]];
                                                                    newRows[rowIndex].file_name = e.target.value;
                                                                    setFormData({
                                                                        ...formData,
                                                                        [db_column_name]: newRows
                                                                    });
                                                                }}
                                                            />
                                                        </td>
                                                        <td>
                                                            <input
                                                                type="text"
                                                                value={row.file_source}
                                                                onChange={(e) => {
                                                                    const newRows = [...formData[db_column_name]];
                                                                    newRows[rowIndex].file_source = e.target.value;
                                                                    setFormData({
                                                                        ...formData,
                                                                        [db_column_name]: newRows
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
