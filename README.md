import React, { useState } from "react";

// Main NavigationView Component
const NavigationView = ({ connectionString }) => {
  const [selectedNode, setSelectedNode] = useState(null);
  const [tableData, setTableData] = useState([]);
  const [title, setTitle] = useState("ESM Dictionary");

  // Handles tree node selection
  const handleTreeNodeSelection = (node) => {
    setSelectedNode(node);
    refreshTable(node);
  };

  // Refresh table based on selected node
  const refreshTable = (node) => {
    if (!node) {
      setTitle("ESM Dictionary");
      setTableData([]);
      return;
    }
    // Update title and data dynamically based on the selected node
    setTitle(node.title || "Selected Node Data");
    setTableData(node.data || []);
  };

  return (
    <div style={{ display: "flex", height: "100%" }}>
      {/* Tree Viewer (Left Panel) */}
      <div style={{ flex: 3, borderRight: "1px solid #ccc", padding: "10px" }}>
        <h3>Tree Viewer</h3>
        <TreeViewer onNodeSelect={handleTreeNodeSelection} />
      </div>

      {/* Table Viewer (Right Panel) */}
      <div style={{ flex: 7, padding: "10px" }}>
        <h3>{title}</h3>
        <TableViewer data={tableData} />
      </div>
    </div>
  );
};

// TreeViewer Component
const TreeViewer = ({ onNodeSelect }) => {
  const mockTreeData = [
    { id: 1, name: "Node 1", data: [{ col1: "A", col2: "B" }] },
    { id: 2, name: "Node 2", data: [{ col1: "C", col2: "D" }] },
  ];

  return (
    <ul>
      {mockTreeData.map((node) => (
        <li key={node.id}>
          <button onClick={() => onNodeSelect(node)}>{node.name}</button>
        </li>
      ))}
    </ul>
  );
};

// TableViewer Component
const TableViewer = ({ data }) => {
  return (
    <table style={{ width: "100%", borderCollapse: "collapse" }}>
      <thead>
        <tr>
          <th>Column 1</th>
          <th>Column 2</th>
        </tr>
      </thead>
      <tbody>
        {data.length > 0 ? (
          data.map((row, index) => (
            <tr key={index}>
              <td>{row.col1}</td>
              <td>{row.col2}</td>
            </tr>
          ))
        ) : (
          <tr>
            <td colSpan="2" style={{ textAlign: "center" }}>
              No data available
            </td>
          </tr>
        )}
      </tbody>
    </table>
  );
};

export default NavigationView;





import React, { useState, useEffect } from "react";

// Mock data for the tree structure (replace this with API data)
const mockTreeData = [
  {
    id: "1",
    label: "Parent Node 1",
    children: [
      { id: "1-1", label: "Child Node 1" },
      { id: "1-2", label: "Child Node 2" },
    ],
  },
  {
    id: "2",
    label: "Parent Node 2",
    children: [
      { id: "2-1", label: "Child Node 3" },
      { id: "2-2", label: "Child Node 4" },
    ],
  },
];

const TreeView = () => {
  const [treeData, setTreeData] = useState([]);
  const [selectedNode, setSelectedNode] = useState(null);

  // Fetch data for the tree (mocked here)
  useEffect(() => {
    // Replace with actual API call
    setTreeData(mockTreeData);
  }, []);

  const handleNodeClick = (node) => {
    console.log("Selected Node:", node);
    setSelectedNode(node);
    // Perform additional actions such as refreshing the table
  };

  const refreshTree = () => {
    console.log("Refreshing tree...");
    // Logic to refresh tree data
    // Replace this with an API call to fetch new data
    setTreeData([...mockTreeData]);
  };

  return (
    <div className="tree-view">
      <div className="tree-view-header">
        <button onClick={refreshTree}>Refresh Tree</button>
      </div>
      <ul>
        {treeData.map((node) => (
          <TreeNode key={node.id} node={node} onNodeClick={handleNodeClick} />
        ))}
      </ul>
      {selectedNode && (
        <div className="selected-node">
          <h4>Selected Node:</h4>
          <p>{selectedNode.label}</p>
        </div>
      )}
    </div>
  );
};

const TreeNode = ({ node, onNodeClick }) => {
  const [isExpanded, setIsExpanded] = useState(false);

  const toggleExpand = () => {
    setIsExpanded(!isExpanded);
  };

  return (
    <li>
      <div className="tree-node" onClick={() => onNodeClick(node)}>
        {node.children && (
          <button onClick={toggleExpand}>
            {isExpanded ? "-" : "+"}
          </button>
        )}
        {node.label}
      </div>
      {isExpanded && node.children && (
        <ul>
          {node.children.map((child) => (
            <TreeNode key={child.id} node={child} onNodeClick={onNodeClick} />
          ))}
        </ul>
      )}
    </li>
  );
};

export default TreeView;

.tree-view {
  font-family: Arial, sans-serif;
}

.tree-view-header {
  margin-bottom: 10px;
}

.tree-node {
  cursor: pointer;
  margin: 5px 0;
}

.tree-node button {
  margin-right: 5px;
}

.selected-node {
  margin-top: 20px;
  border-top: 1px solid #ccc;
  padding-top: 10px;
}








import React, { useState } from 'react';

function MyTableView({ comp, filterPkVal, queryResult }) {
  const [tableData, setTableData] = useState(queryResult?.rows || []);
  const [selectedRow, setSelectedRow] = useState(null);

  const handleDoubleClick = (row) => {
    // Open a modal form for the row
    console.log('Double-clicked row:', row);
    // Add your modal logic here
  };

  const handleCopy = () => {
    const selectedText = tableData.map((row) => row.join('\t')).join('\n');
    navigator.clipboard.writeText(selectedText).then(() => {
      alert('Copied to clipboard');
    });
  };

  const handleDelete = () => {
    if (window.confirm('Do you really want to delete?')) {
      // Perform delete logic
      console.log('Deleted row:', selectedRow);
    }
  };

  const handleAddNew = () => {
    console.log('Add new row');
    // Add logic to add new row
  };

  const handleEdit = () => {
    console.log('Edit row:', selectedRow);
    // Add logic to edit row
  };

  return (
    <div className="my-table-view">
      <div className="header">
        <h3>{comp.getLabel()}</h3>
      </div>
      <table className="table" cellSpacing="0" cellPadding="5">
        <thead>
          <tr>
            {comp.getGridColumns().map((col, idx) => (
              <th key={idx}>{col.getLabel()}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {tableData.map((row, rowIndex) => (
            <tr
              key={rowIndex}
              onDoubleClick={() => handleDoubleClick(row)}
              onClick={() => setSelectedRow(row)}
              className={selectedRow === row ? 'selected' : ''}
            >
              {row.map((cell, cellIndex) => (
                <td key={cellIndex}>{cell}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
      <div className="actions">
        <button onClick={handleCopy}>Copy</button>
        <button onClick={handleAddNew}>Add New</button>
        <button onClick={handleEdit} disabled={!selectedRow}>
          Edit
        </button>
        <button onClick={handleDelete} disabled={!selectedRow}>
          Delete
        </button>
      </div>
    </div>
  );
}

export default MyTableView;


.my-table-view {
  font-family: Arial, sans-serif;
}

.table {
  border-collapse: collapse;
  width: 100%;
}

.table th, .table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.table tr:hover {
  background-color: #f1f1f1;
}

.table .selected {
  background-color: #e0f7fa;
}

.actions {
  margin-top: 10px;
}

.actions button {
  margin-right: 10px;
  padding: 5px 10px;
}







import React from 'react';

const MyStaticTableView = ({ component }) => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <div style={{ flex: 1, textAlign: 'center' }}>
        <h1>ESH Dictionary</h1>
      </div>
      <div style={{ flex: 3, overflow: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr>
              <th style={{ border: '1px solid black', padding: '8px' }}>Header 1</th>
              <th style={{ border: '1px solid black', padding: '8px' }}>Header 2</th>
              <th style={{ border: '1px solid black', padding: '8px' }}>Header 3</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td style={{ border: '1px solid black', padding: '8px' }}>Data 1</td>
              <td style={{ border: '1px solid black', padding: '8px' }}>Data 2</td>
              <td style={{ border: '1px solid black', padding: '8px' }}>Data 3</td>
            </tr>
            {/* Add more rows as needed */}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default MyStaticTableView;






import React, { useState } from 'react';

const ModelTableView = ({ columns, data }) => {
  const [sortState, setSortState] = useState(
    columns.map(() => ({ direction: 'down', reverse: false }))
  );

  const handleSort = (index) => {
    const newSortState = [...sortState];
    newSortState[index].reverse = !newSortState[index].reverse;
    newSortState[index].direction = newSortState[index].reverse ? 'up' : 'down';
    setSortState(newSortState);
  };

  const sortedData = [...data].sort((a, b) => {
    const activeColumn = sortState.findIndex((col) => col.reverse !== null);
    if (activeColumn === -1) return 0;

    const compare =
      a[columns[activeColumn].key].toString() > b[columns[activeColumn].key].toString() ? 1 : -1;
    return sortState[activeColumn].reverse ? -compare : compare;
  });

  return (
    <div style={{ padding: '16px', overflow: 'auto' }}>
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr>
            {columns.map((col, index) => (
              <th
                key={col.key}
                style={{ border: '1px solid black', padding: '8px', cursor: 'pointer' }}
                onClick={() => handleSort(index)}
              >
                {col.name}{' '}
                {sortState[index].direction === 'down' ? (
                  <img src="/icons/arrow_down.gif" alt="Down" />
                ) : (
                  <img src="/icons/arrow_up.gif" alt="Up" />
                )}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {sortedData.map((row, rowIndex) => (
    
        <tr key={rowIndex}>
              {columns.map((col) => (
                <td
                  key={col.key}
                  style={{ border: '1px solid black', padding: '8px', textAlign: 'left' }}
                >
                  {row[col.key]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ModelTableView;



Can some or all of these classes be merged together 
Don't add any additional code 
