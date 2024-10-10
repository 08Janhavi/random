const handleDeleteFileColumn = (dbIndex, fileIndex) => {
    const fileColumn = formData[dbIndex].file_columns[fileIndex];

    // Only proceed if any of the file column fields are filled
    if (fileColumn.file_column_name || fileColumn.file_name || fileColumn.file_source) {
        const payload = {
            databaseName,
            tableName: dbTableName,
            tableColumns: formData.map((dbRow, i) => ({
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

        // Send a DELETE request with the payload
        fetch(`http://localhost:8080/deleteFileColumn`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload),
        })
        .then((response) => {
            if (response.ok) {
                // Update the frontend state after successful deletion
                const updatedFormData = [...formData];
                updatedFormData[dbIndex].file_columns.splice(fileIndex, 1);  // Remove the selected file column

                // If no more file columns left, remove the DB column entirely
                if (updatedFormData[dbIndex].file_columns.length === 0) {
                    updatedFormData.splice(dbIndex, 1);
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
        // Directly update frontend state if no backend call is needed
        const updatedFormData = [...formData];
        updatedFormData[dbIndex].file_columns.splice(fileIndex, 1);  // Remove the selected file column

        // If no more file columns left, remove the DB column entirely
        if (updatedFormData[dbIndex].file_columns.length === 0) {
            updatedFormData.splice(dbIndex, 1);
        }

        setFormData(updatedFormData);
        console.log('File column deleted successfully.');
    }
};
