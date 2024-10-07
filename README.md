// Function to handle deleting a row (either DB or File column)
const handleDeleteFileColumn = (dbIndex, fileIndex) => {
    // Construct the payload similar to handleSubmit
    const payload = {
        databaseName,
        tableName: dbTableName,
        tableColumns: formData.map((dbRow, dbRowIndex) => ({
            columnName: dbRow.db_column_name,
            processName: processName,
            fileColumns: dbRow.file_columns
                .filter((_, i) => !(dbRowIndex === dbIndex && i === fileIndex)) // Exclude the file column being deleted
                .map(fileColumn => ({
                    columnName: fileColumn.file_column_name,
                    fileName: fileColumn.file_name,
                    fileSource: fileColumn.file_source
                }))
        })),
    };

    console.log("Delete payload", payload);

    // Send a DELETE request with the updated payload
    fetch(`http://localhost:8080/deleteFileColumn`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
    })
    .then(response => {
        if (response.ok) {
            // Update the frontend only if the deletion was successful
            const updatedFormData = [...formData];
            updatedFormData[dbIndex].file_columns.splice(fileIndex, 1);  // Remove the selected file column
            setFormData(updatedFormData);
            console.log('File column deleted successfully.');
        } else {
            console.error('Failed to delete the file column.');
        }
    })
    .catch(error => {
        console.error('Error deleting file column:', error);
    });
};
