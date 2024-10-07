const handleSubmit = () => {
  const payload = {
    databaseName,
    tableName: dbTableName,
    tableColumns: [
      {
        columnName: formData[0].db_column_name,
        processName: processName,
        fileColumns: [
          {
            fileColumnName: formData[0].file_columns[0].file_column_name,
            fileName: formData[0].file_columns[0].file_name,
            fileSource: formData[0].file_columns[0].file_source,
          },
        ],
      },
    ],
  };

  console.log("payload", payload);

  fetch("http://localhost:8080/saveColumnMappings", {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then((result) => {
      console.log('Data updated successfully:', result);
      navigate(-1); // Navigate to the previous page
    })
    .catch((error) => {
      console.error('Error updating data:', error);
    });
};
