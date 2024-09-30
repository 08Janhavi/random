import React, { useEffect, useState } from 'react';

const AddEditDataScreen = () => {
    const [data, setData] = useState({
        'DB1': [
            { dbColumnName: 'DB1', fileColumnName: 'fileColumn1', fileName: 'file1.csv', fileSource: 'Source1' },
            { dbColumnName: 'DB1', fileColumnName: 'fileColumn2', fileName: 'file2.csv', fileSource: 'Source2' }
        ],
        'DB2': [
            { dbColumnName: 'DB2', fileColumnName: 'fileColumn3', fileName: 'file3.csv', fileSource: 'Source3' }
        ]
    });

    useEffect(() => {
        fetch('/api/getData')
            .then((response) => response.json())
            .then((data) => {
                const groupedData = groupByDbColumnName(data);
                setData(groupedData);
            })
            .catch((error) => console.error('Error fetching data:', error));
    }, []);

    const groupByDbColumnName = (data) => {
        const grouped = {};
        data.forEach((row) => {
            if (!grouped[row.dbColumnName]) {
                grouped[row.dbColumnName] = [];
            }
            grouped[row.dbColumnName].push(row);
        });
        return grouped;
    };

    const handleDelete = (dbColumnName, index) => {
        const newData = { ...data };
        newData[dbColumnName].splice(index, 1);

        // If no more file column rows exist, delete the whole entry
        if (newData[dbColumnName].length === 0) {
            delete newData[dbColumnName];
        }

        setData(newData);
    };

    const handleAddFileColumn = (dbColumnName) => {
        const newData = { ...data };
        newData[dbColumnName].push({
            dbColumnName,
            fileColumnName: '',
            fileName: '',
            fileSource: ''
        });
        setData(newData);
    };

    const handleAddDbColumn = () => {
        const newDbColumnName = `DB${Object.keys(data).length + 1}`; // Create new db column name (this can be changed)
        const newData = { ...data };

        newData[newDbColumnName] = [
            {
                dbColumnName: newDbColumnName,
                fileColumnName: '',
                fileName: '',
                fileSource: ''
            }
        ];

        setData(newData);
    };

    const handleSubmit = () => {
    };

    const handleCancel = () => {
    };

    return (
        <div className="root">
            <div className="main">
                <div >
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
                                            {Object.entries(data).map(([dbColumnName, rows], dbIndex) => (
                                                <React.Fragment key={dbIndex}>
                                                    {rows.map((row, rowIndex) => (

                                                        <tr key={rowIndex}>
                                                            {rowIndex === 0 && (
                                                                <td rowSpan={rows.length} className='db-column-cell'>{dbColumnName}</td>
                                                            )}
                                                            <td>{row.fileColumnName}</td>
                                                            <td>{row.fileName}</td>
                                                            <td>{row.fileSource}</td>
                                                            <td>
                                                                <button onClick={() => handleDelete(dbColumnName, rowIndex)}>
                                                                    Delete
                                                                </button>
                                                            </td>
                                                        </tr>
                                                    ))}
                                                    {/* Add File Column button after each set of file column details */}
                                                    <tr>
                                                        <td colSpan="5">
                                                            <button
                                                                onClick={() => handleAddFileColumn(dbColumnName)}
                                                                className="add-file-btn"
                                                            >
                                                                Add File Column
                                                            </button>
                                                        </td>
                                                    </tr>
                                                </React.Fragment>
                                            ))}
                                            {/* Add New DB Column button at the end of all rows */}
                                            <tr>
                                                <td colSpan="5">
                                                    <button onClick={handleAddDbColumn} className="add-db-btn">
                                                        Add DB Column
                                                    </button>
                                                </td>
                                            </tr>
                                        </tbody>

                                    </table>
                                    <table className="table-xml">
                                        <tbody>
                                            <tr>
                                                <td>
                                                    <button onClick={handleSubmit} className='btn submit-btn'>Submit</button>
                                                    <button onClick={handleCancel} className='btn cancel-btn'>Cancel</button>
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
        </div>
    );
};

export default AddEditDataScreen;
