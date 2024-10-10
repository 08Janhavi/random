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
