import React, { useEffect, useState } from 'react';
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
                    const structuredData = structureData(data);
                    setData(structuredData);
                })
                .catch((error) => console.error('Error fetching data:', error));
        }
    }, [databaseName, dbTableName]);

    const structureData = (data) => {
        const result = [];
        const table = data[0];

        if (table && table.tableColumns) {
            table.tableColumns.forEach((column) => {
                const fileColumns = column.fileColumns.map(fileCol => ({
                    file_column_name: fileCol.columnName,
                    file_name: fileCol.fileName,
                    file_source: fileCol.fileSource,
                }));
                result.push({
                    db_column_name: column.columnName,
                    rows: fileColumns,
                });
            });
        }
        return result;
    };

    const handleEdit = (item) => {
        navigate('/addEditDataScreen', {
            state: {
                databaseName, // The selected database name
                dbTableName: item.db_column_name // Pass the db_column_name as needed
            }
        });
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
                                                <th>Actions</th> {/* Added Actions column for Edit button */}
                                            </tr>
                                            {data.map((item, index) => (
                                                <React.Fragment key={index}>
                                                    {item.rows.map((row, rowIndex) => (
                                                        <tr key={rowIndex}>
                                                            {rowIndex === 0 && (
                                                                <td rowSpan={item.rows.length} className='db-column-cell'>{item.db_column_name}</td>
                                                            )}
                                                            <td>{row.file_column_name}</td>
                                                            <td>{row.file_name}</td>
                                                            <td>{row.file_source}</td>
                                                            {rowIndex === 0 && (
                                                                <td rowSpan={item.rows.length}>
                                                                    <button onClick={() => handleEdit(item)} className='btn edit-btn'>Edit</button>
                                                                </td>
                                                            )}
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
    );
};

export default ViewDataScreen;
