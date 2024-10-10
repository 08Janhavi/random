const handleDeleteFileColumn = (dbIndex, fileIndex) => {
    if (!formData[dbIndex]) {
        console.error(`Invalid dbIndex: ${dbIndex}`);
        return;
    }

    const fileColumns = formData[dbIndex].file_columns;
    
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
                fileColumns: dbRow.file_columns.map((fileColumn) => ({
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
                updatedFormData[dbIndex].file_columns.splice(fileIndex, 1);  // Remove the selected file column

                if (updatedFormData[dbIndex].file_columns.length === 0) {
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
        updatedFormData[dbIndex].file_columns.splice(fileIndex, 1);  // Remove the selected file column

        if (updatedFormData[dbIndex].file_columns.length === 0) {
            updatedFormData.splice(dbIndex, 1); // Remove the DB column if it has no file columns left
        }

        setFormData(updatedFormData);
        console.log('File column deleted successfully.');
    }
};
