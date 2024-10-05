import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const AddEditDataScreen = () => {
    const location = useLocation();
    const navigate = useNavigate();

    // Extract db_column_name, row, etc. from location.state or set empty defaults
    const { db_column_name = '', row = [] } = location.state || {};

    // Initialize formData with the correct structure
    const [formData, setFormData] = useState([{
        db_column_name: db_column_name || '',
        file_column_name: row?.file_column_name || '',
        file_name: row?.file_name || '',
        file_source: row?.file_source || '',
    }]);

    // Load data into formData when the component mounts or when location state changes
    useEffect(() => {
        if (location.state) {
            setFormData([{
                db_column_name: db_column_name || '',
                file_column_name: row?.file_column_name || '',
                file_name: row?.file_name || '',
                file_source: row?.file_source || '',
            }]);
        }
    }, [location.state, db_column_name, row]);

    // Function to handle form input changes
    const handleInputChange = (index, e) => {
        const { name, value } = e.target;
        const updatedFormData = [...formData];
        updatedFormData[index] = {
            ...updatedFormData[index],
            [name]: value,
        };
        setFormData(updatedFormData);
    };

    // Function to handle form submission
    const handleSubmit = () => {
        fetch("http://localhost:8080/updateData", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData),
        })
            .then((response) => response.json())
            .then((result) => {
                console.log('Data updated successfully:', result);
                navigate(-1);  // Go back after submission
            })
            .catch((error) => console.error('Error updating data:', error));
    };

    // Function to handle adding a new file column (row) to the form
    const handleAddFileColumn = () => {
        setFormData((prevFormData) => [
            ...prevFormData,
            {
                db_column_name: db_column_name || '',
                file_column_name: '',
                file_name: '',
                file_source: '',
            }
        ]);
    };

    // Function to handle adding a new DB column (a new form row)
    const handleAddDbColumn = () => {
        const newDbColumnName = `DB_${formData.length + 1}`;
        setFormData((prevFormData) => [
            ...prevFormData,
            {
                db_column_name: newDbColumnName,
                file_column_name: '',
                file_name: '',
                file_source: '',
            }
        ]);
    };

    // Function to handle deleting a row
    const handleDelete = (index) => {
        const updatedFormData = [...formData];
        updatedFormData.splice(index, 1);  // Remove the selected row
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
                                        {formData.map((row, index) => (
                                            <tr key={index}>
                                                <td>
                                                    <input
                                                        type="text"
                                                        name="db_column_name"
                                                        value={row.db_column_name}
                                                        onChange={(e) => handleInputChange(index, e)}
                                                        disabled // Disable editing of db_column_name
                                                    />
                                                </td>
                                                <td>
                                                    <input
                                                        type="text"
                                                        name="file_column_name"
                                                        value={row.file_column_name}
                                                        onChange={(e) => handleInputChange(index, e)}
                                                    />
                                                </td>
                                                <td>
                                                    <input
                                                        type="text"
                                                        name="file_name"
                                                        value={row.file_name}
                                                        onChange={(e) => handleInputChange(index, e)}
                                                    />
                                                </td>
                                                <td>
                                                    <input
                                                        type="text"
                                                        name="file_source"
                                                        value={row.file_source}
                                                        onChange={(e) => handleInputChange(index, e)}
                                                    />
                                                </td>
                                                <td>
                                                    <button onClick={() => handleDelete(index)}>
                                                        Delete
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                        <tr>
                                            <td colSpan="5">
                                                <button
                                                    onClick={handleAddFileColumn}
                                                    className="add-file-btn"
                                                >
                                                    Add File Column
                                                </button>
                                            </td>
                                        </tr>
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
