import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const AddEditDataScreen = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const { databaseName, dbTableName, processName, data } = location.state || {};

    const [formData, setFormData] = useState(data || []);

    useEffect(() => {
        if (location.state) {
            setFormData(data || []);
        }
    }, [location.state, data]);

    const handleInputChange = (dbIndex, fileIndex, e) => {
        const { name, value } = e.target;
        const updatedFormData = [...formData];
        
        if (fileIndex === null) {
            updatedFormData[dbIndex] = {
                ...updatedFormData[dbIndex],
                [name]: value,
            };
        } else {
            updatedFormData[dbIndex].rows[fileIndex] = {
                ...updatedFormData[dbIndex].rows[fileIndex],
                [name]: value,
            };
        }
        setFormData(updatedFormData);
    };

    const handleSubmit = () => {
        for (let dbRow of formData) {
            for (let fileColumn of dbRow.rows) {
                if (!fileColumn.file_column_name || !fileColumn.file_name || !fileColumn.file_source) {
                    alert("File column name, file name, and file source cannot be empty");
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
                fileColumns: dbRow.rows.map((fileColumn) => ({
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

    const handleAddFileColumn = (dbIndex) => {
        const updatedFormData = [...formData];
        updatedFormData[dbIndex].rows.push({
            file_column_name: '',
            file_name: '',
            file_source: ''
        });
        setFormData(updatedFormData);
    };

    const handleAddDbColumn = () => {
        setFormData((prevFormData) => [
            ...prevFormData,
            {
                db_column_name: '',
                rows: [{ file_column_name: '', file_name: '', file_source: '' }]
            }
        ]);
    };

    



    const handleDeleteFileColumn = (dbIndex, fileIndex) => {
        console.log(formData)
        console.log(formData[dbIndex].rows)
        if (!formData[dbIndex]) {
            console.error(`Invalid dbIndex: ${dbIndex}`);
            return;
        }
    
        const fileColumns = formData[dbIndex].rows;
        
        if (!fileColumns || !fileColumns[fileIndex]) {
            console.error(`Invalid fileIndex: ${fileIndex}`);
            return;
        }
    
        const fileColumn = fileColumns[fileIndex];
    
        if (fileColumn.file_column_name || fileColumn.file_name || fileColumn.file_source) {
            const payload = {
                databaseName,
                tableName: dbTableName,
                tableColumns: formData.map((dbRow) => ({
                    columnName: dbRow.db_column_name,
                    processName: processName,
                    fileColumns: dbRow.rows.map((fileColumn) => ({
                        columnName: fileColumn.file_column_name,
                        fileName: fileColumn.file_name,
                        fileSource: fileColumn.file_source,
                    })),
                })),
            };
    
            console.log("Delete payload", payload);
    
            fetch(`http://localhost:8080/deleteFileColumn`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
            })
            .then((response) => {
                if (response.ok) {
                    const updatedFormData = [...formData];
                    updatedFormData[dbIndex].rows.splice(fileIndex, 1);  // Remove the selected file column
    
                    if (updatedFormData[dbIndex].rows.length === 0) {
                        updatedFormData.splice(dbIndex, 1); // Remove the DB column if it has no file columns left
                    }
    
                    setFormData(updatedFormData);
                    console.log('File column deleted successfully.');
                } else {
                    console.error('Failed to delete the file column.');
                }
            })
            .catch((error) => {
                console.error('Error deleting file column:', error);
            });
        } else {
            const updatedFormData = [...formData];
            updatedFormData[dbIndex].rows.splice(fileIndex, 1);  // Remove the selected file column
    
            if (updatedFormData[dbIndex].rows.length === 0) {
                updatedFormData.splice(dbIndex, 1); // Remove the DB column if it has no file columns left
            }
    
            setFormData(updatedFormData);
            console.log('File column deleted successfully.');
        }
    };

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
                                                {dbRow.rows.length > 0 && (
                                                    <>
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
                                                {dbRow.rows.map((fileRow, fileIndex) => (
                                                    <tr key={fileIndex}>
                                                        <td></td>
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
                                                {dbRow.rows.length > 0 && (
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
