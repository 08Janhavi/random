import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const AddEditDataScreen = () => {
    const location = useLocation();
    const navigate = useNavigate();

    // Extract db_column_name, row, etc. from location.state or set empty defaults
    const { db_column_name = '', row = [] } = location.state || {};

    // Initialize formData with the correct structure
    const [formData, setFormData] = useState({
        db_column_name: db_column_name || '',
        file_column_name: row?.file_column_name || '',
        file_name: row?.file_name || '',
        file_source: row?.file_source || '',
    });

    // Load data into the formData when the component mounts or when location state changes
    useEffect(() => {
        if (location.state) {
            setFormData({
                db_column_name: db_column_name || '',
                file_column_name: row?.file_column_name || '',
                file_name: row?.file_name || '',
                file_source: row?.file_source || '',
            });
        }
    }, [location.state, db_column_name, row]);

    // Function to handle form input changes
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevFormData) => ({
            ...prevFormData,
            [name]: value,
        }));
    };

    // Function to handle form submission
    const handleSubmit = () => {
        const submitData = [formData];  // Send formData as the submission data
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

    // Function to handle cancel action
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
                                        </tr>
                                        <tr>
                                            <td>
                                                <input
                                                    type="text"
                                                    name="db_column_name"
                                                    value={formData.db_column_name}
                                                    onChange={handleInputChange}
                                                    disabled // Disable editing of db_column_name
                                                />
                                            </td>
                                            <td>
                                                <input
                                                    type="text"
                                                    name="file_column_name"
                                                    value={formData.file_column_name}
                                                    onChange={handleInputChange}
                                                />
                                            </td>
                                            <td>
                                                <input
                                                    type="text"
                                                    name="file_name"
                                                    value={formData.file_name}
                                                    onChange={handleInputChange}
                                                />
                                            </td>
                                            <td>
                                                <input
                                                    type="text"
                                                    name="file_source"
                                                    value={formData.file_source}
                                                    onChange={handleInputChange}
                                                />
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
