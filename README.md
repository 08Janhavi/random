// Function to handle deleting a row (either DB or File column)
const handleDeleteFileColumn = (dbIndex, fileIndex) => {
    const db_column_name = formData[dbIndex].db_column_name;
    const file_column_name = formData[dbIndex].file_columns[fileIndex].file_column_name;

    // Send a DELETE request to the backend
    fetch(`http://localhost:8080/deleteFileColumn?db_column_name=${db_column_name}&file_column_name=${file_column_name}`, {
        method: 'DELETE',
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
