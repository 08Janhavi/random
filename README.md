import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const AddEditDataScreen = () => {
    const location = useLocation();
    const navigate = useNavigate();
    
    const { dbColumnName = '', rows = [] } = location.state || {};
    const [data, setData] = useState(rows);
    const [newDbColumn, setNewDbColumn] = useState('');
    const [newFileCol, setNewFileCol] = useState({
        file_column_name: '',
        file_name: '',
        file_source: ''
    });

    const handleSave = () => {
        const updatedData = {
            dbColumnName,
            rows: data,
        };
        
        // Send the updated data to the server
        fetch(`http://localhost:8080/updateData`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(updatedData),
        })
        .then(response => response.json())
        .then(result => {
            console.log('Data updated successfully:', result);
            navigate(-1); // Go back after submission
        })
        .catch(error => console.error('Error updating data:', error));
    };

    const handleCancel = () => {
        navigate(-1); // Go back without saving
    };

    // Function to delete a specific file column row
    const handleDeleteRow = (index) => {
        const newData = data.filter((_, i) => i !== index);
        setData(newData);
    };

    // Function to add a new file column row for the current db column
    const handleAddFileColumn = () => {
        const newData = [...data, { ...newFileCol }];
        setData(newData);
        setNewFileCol({ file_column_name: '', file_name: '', file_source: '' });
    };

    // Function to add a new db column with an initial file column
    const handleAddDbColumn = () => {
        if (newDbColumn) {
            // Assuming you manage multiple dbColumns at a higher level
            const newDbData = {
                db_column_name: newDbColumn,
                rows: [newFileCol]
            };
            // Save the newDbData somewhere or handle it here
            console.log('New DB Column:', newDbData);
            setNewDbColumn('');
            setNewFileCol({ file_column_name: '', file_name: '', file_source: '' });
        }
    };

    return (
        <div>
            <h2>Edit Data for {dbColumnName || 'New DB Column'}</h2>
            <table>
                <thead>
                    <tr>
                        <th>File Column Name</th>
                        <th>File Name</th>
                        <th>File Source</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {data.map((row, index) => (
                        <tr key={index}>
                            <td>
                                <input
                                    type="text"
                                    value={row.file_column_name}
                                    onChange={(e) => {
                                        const newData = [...data];
                                        newData[index].file_column_name = e.target.value;
                                        setData(newData);
                                    }}
                                />
                            </td>
                            <td>
                                <input
                                    type="text"
                                    value={row.file_name}
                                    onChange={(e) => {
                                        const newData = [...data];
                                        newData[index].file_name = e.target.value;
                                        setData(newData);
                                    }}
                                />
                            </td>
                            <td>
                                <input
                                    type="text"
                                    value={row.file_source}
                                    onChange={(e) => {
                                        const newData = [...data];
                                        newData[index].file_source = e.target.value;
                                        setData(newData);
                                    }}
                                />
                            </td>
                            <td>
                                <button onClick={() => handleDeleteRow(index)}>Delete</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            
            {/* Add File Column Section */}
            <div>
                <h3>Add File Column</h3>
                <input
                    type="text"
                    placeholder="File Column Name"
                    value={newFileCol.file_column_name}
                    onChange={(e) => setNewFileCol({ ...newFileCol, file_column_name: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="File Name"
                    value={newFileCol.file_name}
                    onChange={(e) => setNewFileCol({ ...newFileCol, file_name: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="File Source"
                    value={newFileCol.file_source}
                    onChange={(e) => setNewFileCol({ ...newFileCol, file_source: e.target.value })}
                />
                <button onClick={handleAddFileColumn}>Add File Column</button>
            </div>

            {/* Add DB Column Section */}
            <div>
                <h3>Add DB Column</h3>
                <input
                    type="text"
                    placeholder="DB Column Name"
                    value={newDbColumn}
                    onChange={(e) => setNewDbColumn(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="File Column Name"
                    value={newFileCol.file_column_name}
                    onChange={(e) => setNewFileCol({ ...newFileCol, file_column_name: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="File Name"
                    value={newFileCol.file_name}
                    onChange={(e) => setNewFileCol({ ...newFileCol, file_name: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="File Source"
                    value={newFileCol.file_source}
                    onChange={(e) => setNewFileCol({ ...newFileCol, file_source: e.target.value })}
                />
                <button onClick={handleAddDbColumn}>Add DB Column</button>
            </div>

            <button onClick={handleSave}>Save</button>
            <button onClick={handleCancel}>Cancel</button>
        </div>
    );
};

export default AddEditDataScreen;
