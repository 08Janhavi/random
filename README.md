const handleMenuClick = async (optionLabel) => {
    setSelectedoption(optionLabel);

    try {
        // First POST request
        const response = await axios.post("http://localhost:8080/api/handleSelection", optionLabel, {
            headers: {
                "Content-Type": "text/plain",
            },
        });

        setResponseMessage(response.data);

        // Fetch column names after the POST request
        const columnResponse = await fetch("http://localhost:8080/api/getColumnNames");
        const columnData = await columnResponse.json();
        setColumnNames(columnData.colNames); // Assuming backend sends { colNames: ["Code", "Description"] }

        // Fetch table data after fetching column names
        const tableResponse = await fetch("http://localhost:8080/api/getTableData");
        const tableData = await tableResponse.json();
        setData(tableData); // Assuming tableData is an array of objects
    } catch (error) {
        console.error("Error handling menu selection or fetching data:", error);
        setResponseMessage("Error processing the selected option.");
    }
};
