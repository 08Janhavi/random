const handleDeleteFileColumn = (dbIndex, fileIndex) => {
        const fileColumn=formData[dbIndex].file_columns[fileIndex];
    // Construct the payload similar to handleSubmit
    if(fileColumn.file_column_name || fileColumn.file_name || fileColumn.file_source){
        const payload = {
            databaseName,
            tableName: dbTableName,
            tableColumns: formData.map((dbRow) => ({
                columnName: dbRow.db_column_name,
                processName: processName,
                fileColumns: dbRow.file_columns
                    .map((fileColumn ,i)=> ({
                        columnName: fileColumn.file_column_name,
                        fileName: fileColumn.file_name,
                        fileSource: fileColumn.file_source,
                    })),
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
            if (updatedFormData[dbIndex].file_columns.length === 0) {
                updatedFormData.splice(dbIndex, 1); // Remove the DB column if it has no file columns left
            }
    
            setFormData(updatedFormData);
            console.log('File column deleted successfully.');
            navigate(-1);
        } else {
            console.error('Failed to delete the file column.');
        }
    })
    .catch(error => {
        console.error('Error deleting file column:', error);
    });
    }
    else{
        const updatedFormData = [...formData];
            updatedFormData[dbIndex].file_columns.splice(fileIndex, 1);  // Remove the selected file column
            if (updatedFormData[dbIndex].file_columns.length === 0) {
                updatedFormData.splice(dbIndex, 1); // Remove the DB column if it has no file columns left
            }
    
            setFormData(updatedFormData);
            console.log('File column deleted successfully.');
    }
    };
