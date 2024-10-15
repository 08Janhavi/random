Here's how you can document the **frontend** of your project, detailing the core components and functionality. This documentation will explain the structure and processes, making it easy to understand for other team members or stakeholders.

### Frontend Documentation for **Add/Edit Data Screen**

#### **1. Main Components**

- **`AddEditDataScreen` Component**  
   This component allows users to add, edit, and delete database and file columns. It renders a form where users can input database column names and associated file column data.

#### **2. Key Features and Functions**

- **State Management**  
  The state is managed using React hooks:
  - `formData`: Stores the dynamic form data for database columns and file columns.
  - `useNavigate`: Provides navigation functionality to switch between different screens.

- **Event Handlers**  
  - **`handleInputChange(dbIndex, fileIndex, e)`**: Handles input changes for both DB and file columns, updating the corresponding data in the state.
  - **`handleAddDbColumn`**: Adds a new database column to the form.
  - **`handleAddFileColumn(dbIndex)`**: Adds a new file column to the specified database column.
  - **`handleDeleteFileColumn(dbIndex, fileIndex)`**: Deletes the specified file column.
  - **`handleSubmit`**: Handles form submission.
  - **`handleCancel`**: Navigates back without saving.

#### **3. Structure of the UI**

The component structure is as follows:
- **Table Structure**  
  Displays the form with each database column and its associated file columns.  
  - For each database column:
    - A text input for the DB column name.
    - File columns associated with the DB column, each with inputs for `File Column Name`, `File Name`, and `File Source`.
  - The table has buttons to add and delete file columns dynamically.
  
- **Form Controls**  
  - The form has buttons for:
    - **Add New DB Column**: Adds a new database column.
    - **Submit**: Submits the form data.
    - **Cancel**: Cancels the operation and navigates back.

#### **4. Important Functional Sections**

- **Dynamic Form Rendering**
  - The form data is rendered dynamically based on the `formData` state. Each database column can have multiple associated file columns, and these are handled using nested maps.

- **State and Props**
  - `formData` maintains the array of DB columns, each having rows of file columns. This ensures flexibility and scalability for handling multiple columns.

#### **5. `ViewDataScreen` Component**

The `ViewDataScreen` component displays data from the database and provides the ability to select and view data for a specific database and table.

- **Key Features**:
  - Uses `localStorage` to persist the selected `databaseName` and `dbTableName` across sessions.
  - Fetches databases and tables using APIs.
  - Displays the column mappings for the selected database and table in a tabular format.

#### **6. Component Relationships**
- The `ViewDataScreen` fetches and displays the data, while `AddEditDataScreen` handles the editing and adding of database and file columns. The two components are linked by routing, using `useNavigate` for navigating between them.

---

### Visual Representation

You can add screenshots or images of the following sections:
1. **Add/Edit Data Screen**: Display the form with DB columns and file columns.
2. **View Data Screen**: Display the data table showing columns and associated file details.

---

This documentation provides a clear understanding of the frontend's behavior and structure. You can now integrate this into your Confluence page for easy reference. Let me know if you'd like to modify or add more details!




Your backend is built using Spring Boot and manages lineage data in a relational database. Here's a breakdown of its structure:

### 1. **Controller Layer:**
   - **`LineageDataController`**: This class is a REST controller that handles HTTP requests and provides endpoints for retrieving and manipulating lineage data.
     - **`/getDatabases`**: Retrieves all database names.
     - **`/getTables`**: Retrieves all table names.
     - **`/getColumnMappings`**: Retrieves column mappings for a specific database and table.
     - **`/saveColumnMappings`**: Saves column mappings (inserts or updates).
     - **`/deleteFileColumn`**: Deletes file column mappings for a given table.

### 2. **DAO Layer (Data Access Object):**
   - **`IMLineageDataDAO`**: This class handles database operations using JDBC. It interacts directly with the database, performing CRUD operations on the lineage data tables.
     - **Queries**:
       - `getAllDatabases()`: Fetches distinct database names.
       - `getAllTables()`: Fetches distinct table names.
       - `getLineageDataFromDB()`: Fetches lineage data based on database and table.
       - **`saveLineageData()`**: Inserts or updates table and column mappings.
       - **`deleteFileColumn()`**: Deletes file columns and associated data.
  
### 3. **Service Layer:**
   - **`GenerateService`**: This service interacts with the DAO to fetch lineage data.
   - **`IMLineageGeneratorService`**: Interface for the service layer.

### 4. **Data Model:**
   - **`Table`**: Represents a database table and its columns.
   - **`TableColumn`**: Represents a column in a table and its associated file columns.
   - **`FileColumn`**: Represents a file column with its name, source file, and source details.

### 5. **Main Application Class:**
   - **`IMDataLineageApplication`**: The entry point for the Spring Boot application.

The code effectively integrates with a database and provides a RESTful API for managing lineage data with logging for debugging. Would you like more details on a specific part of this backend?
