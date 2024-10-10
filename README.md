import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';


const ViewDataScreen = () => {
    const [databaseName, setDatabaseName] = useState(()=>{
        return localStorage.getItem('databaseName') || '';
    });
    const [dbTableName, setDbTableName] = useState(()=>{
        return localStorage.getItem('dbTableName') || '';
    });
    const [data, setData] = useState([]);
    const [databases, setDatabases] = useState([]);
    const [dbTables, setDbTables] = useState([]);
    const navigate = useNavigate();

    useEffect(()=>{
        localStorage.setItem('databaseName',databaseName);
    },[databaseName]);

    useEffect(()=>{
        localStorage.setItem('dbTableName',dbTableName);
    },[dbTableName]);
    
    useEffect(()=>{
        fetch(`http://localhost:8080/getDatabases`)
        .then((response)=>response.json())
        .then((data)=>{
            setDatabases(data);
        })
        .catch((error) => console.error("error fetching databases",error));
    },[]);

    useEffect(()=>{
        fetch(`http://localhost:8080/getTables`)
        .then((response)=>response.json())
        .then((data)=>{
            setDbTables(data);
        })
        .catch((error) => console.error("error fetching tables",error));
    },[]);

    useEffect(() => {
        console.log(databaseName,dbTableName);
        if (databaseName && dbTableName) {
            fetch(`http://localhost:8080/getColumnMappings?db=${databaseName}&table=${dbTableName}&_=${new Date().getTime()}`)
                .then((response) => response.json())
                .then((data) => {
                    const structuredData = structureData(data);
                    setData(structuredData);
                })
                .catch((error) => console.error('Error fetching data:', error));
        }
    }, [databaseName, dbTableName]);

    const [processName,setProcessName]=useState("");

    const structureData= (data) => {
        const result = [];
        const table = data[0];

        if(table && table.tableColumns){
            table.tableColumns.forEach((column) =>{
                const fileColumns=column.fileColumns.map(fileCol => ({
                    file_column_name:fileCol.columnName,
                    file_name:fileCol.fileName,
                    file_source:fileCol.fileSource,
                }));
                setProcessName(column.processName);
                console.log(column.processName);
                result.push({
                    db_column_name:column.columnName,
                    rows:fileColumns,
                });
            });
        }
        return result;
    };

    // const groupByDbColumnName = (data) => {
    //     const grouped = {};
    //     data.forEach((row) => {
    //         if (!grouped[row.db_column_name]) {
    //             grouped[row.db_column_name] = [];
    //         }
    //         grouped[row.db_column_name].push(row);
    //     });
    //     return grouped;
    // };

    const handleEdit = () => {
        navigate('/addEditDataScreen', { state: { databaseName, dbTableName,processName } });
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
                                            {databases.map((db)=>(
                                                <option key={db} value={db}>{db}</option>
                                            ))}
                                        </select>
                                    </label>
                                    <label>
                                        DB Table Name:
                                        <select value={dbTableName} onChange={(e) => setDbTableName(e.target.value)}>
                                            <option value="">Select Table</option>
                                            {dbTables.map((table)=>(
                                                <option key={table} value={table}>{table}</option>
                                            ))}
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
                                            {data.map((item,index) => (
                                                <React.Fragment key={index}>
                                                    {item.rows.map((row, rowIndex) => (
                                                        <tr key={`${index}-${rowIndex}`}>
                                                            {rowIndex === 0 && (
                                                                <td rowSpan={item.rows.length} className='db-column-cell'>{item.db_column_name}</td>
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
                                                    
                                                <button onClick={()=> handleEdit()} className='btn edit-btn'>Edit</button>
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
