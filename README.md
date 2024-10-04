import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const AddEditDataScreen = () => {
    const location = useLocation();
    const navigate = useNavigate();
    
    // Extract db_column_name, rows, etc. from location.state or set empty defaults
    const { db_column_name = '', row = [] } = location.state || {};
    
    const [data, setData] = useState([]);  // Initialize with empty array
    const [formData, setFormData] = useState({
        db_column_name: db_column_name || '',
        file_column_name: row?.file_column_name || '',
        file_name: row?.file_name || '',
        file_source: row?.file_source || '',
    });

    // Initial load of existing data
    useEffect(() => {
        if (location.state) {
            setData([{
                db_column_name,
                file_column_name: row?.file_column_name || '',
                file_name: row?.file_name || '',
                file_source: row?.file_source || '',
            }]);
        }
    }, [location.state, db_column_name, row]);

    // Function to handle deletion of a row
    const handleDelete = (dbColumnName, index) => {
        const newData = { ...data };
        newData[dbColumnName].splice(index, 1);  // Remove the selected row
        if (newData[dbColumnName].length === 0) {
            delete newData[dbColumnName];  // Remove key if no rows left
        }
        setData(newData);  // Update state
    };

    // Function to add a blank file column for the specific DB column
    const handleAddFileColumn = (dbColumnName) => {
        const newData = [...data];
        const newRow = {
            db_column_name: dbColumnName,  // Only keep the DB column name
            file_column_name: '',
            file_name: '',
            file_source: ''
        };
        setData([...newData, newRow]);  // Add the new blank row
    };

    // Function to add a new DB column
    const handleAddDbColumn = () => {
        const newDbColumnName = `DB_${Object.keys(data).length + 1}`;
        const newData = [...data];
        const newRow = {
            db_column_name: newDbColumnName,
            file_column_name: '',
            file_name: '',
            file_source: ''
        };
        setData([...newData, newRow]);  // Add the new blank DB column row
    };

    // Handle form submission
    const handleSubmit = () => {
        // Prepare data for submission
        const submitData = [...data];  // Copy the data state
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
                                        {data.map((row, index) => (
                                            <React.Fragment key={index}>
                                                <tr>
                                                    <td>
                                                        <input
                                                            type="text"
                                                            value={row.db_column_name}
                                                            onChange={(e) =>
                                                                setFormData({
                                                                    ...formData,
                                                                    db_column_name: e.target.value,
                                                                })
                                                            }
                                                            disabled // Disable editing of db_column_name
                                                        />
                                                    </td>
                                                    <td>
                                                        <input
                                                            type="text"
                                                            value={row.file_column_name}
                                                            onChange={(e) =>
                                                                setFormData({
                                                                    ...formData,
                                                                    file_column_name: e.target.value,
                                                                })
                                                            }
                                                        />
                                                    </td>
                                                    <td>
                                                        <input
                                                            type="text"
                                                            value={row.file_name}
                                                            onChange={(e) =>
                                                                setFormData({
                                                                    ...formData,
                                                                    file_name: e.target.value,
                                                                })
                                                            }
                                                        />
                                                    </td>
                                                    <td>
                                                        <input
                                                            type="text"
                                                            value={row.file_source}
                                                            onChange={(e) =>
                                                                setFormData({
                                                                    ...formData,
                                                                    file_source: e.target.value,
                                                                })
                                                            }
                                                        />
                                                    </td>
                                                    <td>
                                                        <button onClick={() => handleDelete(row.db_column_name, index)}>
                                                            Delete
                                                        </button>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td colSpan="5">
                                                        <button
                                                            onClick={() => handleAddFileColumn(row.db_column_name)}
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
