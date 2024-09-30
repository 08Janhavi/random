import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';


const ViewDataScreen = () => {
    const [databaseName, setDatabaseName] = useState('');
    const [dbTableName, setDbTableName] = useState('');
    const [data, setData] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        if (databaseName && dbTableName) {
            fetch(`http://localhost:8080/getColumnMappings?db=${databaseName}&table=${dbTableName}`)
                .then((response) => response.json())
                .then((data) => {
                    const groupedData = groupByDbColumnName(data);
                    setData(groupedData);
                })
                .catch((error) => console.error('Error fetching data:', error));
        }
    }, [databaseName, dbTableName]);

    const groupByDbColumnName = (data) => {
        const grouped = {};
        data.forEach((row) => {
            if (!grouped[row.db_column_name]) {
                grouped[row.db_column_name] = [];
            }
            grouped[row.db_column_name].push(row);
        });
        return grouped;
    };

    const handleEdit = () => {
        navigate('/addEditDataScreen', { state: { databaseName, dbTableName } });
    };

    const handleClose = () => {
        window.close();
    };

    return (
        <>
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
                                        <b>View Data Screen</b>
                                    </span>

                                </div>
                                <div className="dropdowns-container">
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
                    </div>
                </div>
            </div>
        </>
    )
}

export default ViewDataScreen;
