import React, { useState, useEffect } from 'react';
import axios from 'axios';

const ViewDataScreen = () => {
    const [databaseName, setDatabaseName] = useState('');  // Selected database
    const [dbTableName, setDbTableName] = useState('');    // Selected table
    const [data, setData] = useState({});                  // Data fetched from the backend
    const [loading, setLoading] = useState(false);         // Loading state
    const [error, setError] = useState(null);              // Error state

    // Fetch data when databaseName or dbTableName changes
    useEffect(() => {
        if (databaseName && dbTableName) {
            fetchData(databaseName, dbTableName);
        }
    }, [databaseName, dbTableName]);

    // Function to fetch data from the backend
    const fetchData = async (database, table) => {
        setLoading(true);
        setError(null);

        try {
            // Replace with your actual API endpoint
            const response = await axios.get(`/api/data?database=${database}&table=${table}`);
            setData(response.data);  // Set the fetched data
            setLoading(false);
        } catch (err) {
            setError('Failed to fetch data');
            setLoading(false);
        }
    };

    // Handle edit button click (placeholder function)
    const handleEdit = () => {
        console.log('Edit button clicked');
    };

    // Handle close button click (placeholder function)
    const handleClose = () => {
        console.log('Close button clicked');
    };

    return (
        <>
            <div className="container">
                <div className="content">
                    <div className="form-group">
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
                                <option value="dbo.lineage_data_db_tables">rawdata.dbo.lineage_data_db_tables</option>
                                <option value="Table2">Table2</option>
                                <option value="Table3">Table3</option>
                            </select>
                        </label>
                    </div>
                    <div className="highlight">
                        {/* Show loading message */}
                        {loading && <p>Loading data...</p>}

                        {/* Show error message if any */}
                        {error && <p>{error}</p>}

                        {/* Show data when available */}
                        {!loading && !error && Object.keys(data).length > 0 && (
                            <table className="headTable">
                                <tbody>
                                    <tr>
                                        <th>DB Column Name</th>
                                        <th>File Column Name</th>
                                        <th>File Name</th>
                                        <th>File Source</th>
                                    </tr>
                                    {Object.entries(data).map(([db_column_name, rows], dbIndex) => (
                                        <React.Fragment key={dbIndex}>
                                            {rows.map((row, rowIndex) => (
                                                <tr key={rowIndex}>
                                                    {rowIndex === 0 && (
                                                        <td rowSpan={rows.length} className='db-column-cell'>{db_column_name}</td>
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
                        )}

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
        </>
    );
}

export default ViewDataScreen;
