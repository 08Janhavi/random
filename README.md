useEffect(() => {
    // Call the backend navigation view controller service on component mount
    const fetchNavigationData = async () => {
      try {
        const response = await axios.get('/api/navigationView');  // Adjust URL as needed
        console.log(response.data);  // Handle the response as needed
        // You can set any state with the fetched data if required, for example:
        // setNavigationData(response.data);
      } catch (error) {
        console.error("Error fetching navigation data", error);
      }
    };
