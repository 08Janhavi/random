import React, { useState, useEffect } from "react";

// Combined TreeTableView Component
const TreeTableView = () => {
  const [treeData, setTreeData] = useState([]);
  const [selectedNode, setSelectedNode] = useState(null);
  const [tableData, setTableData] = useState([]);

  useEffect(() => {
    // Mock data fetch for tree
    setTreeData([
      {
        id: "1",
        label: "Parent Node 1",
        children: [
          { id: "1-1", label: "Child Node 1", data: [{ col1: "A", col2: "B" }] },
          { id: "1-2", label: "Child Node 2", data: [{ col1: "C", col2: "D" }] },
        ],
      },
      {
        id: "2",
        label: "Parent Node 2",
        children: [
          { id: "2-1", label: "Child Node 3", data: [{ col1: "E", col2: "F" }] },
          { id: "2-2", label: "Child Node 4", data: [{ col1: "G", col2: "H" }] },
        ],
      },
    ]);
  }, []);

  const handleNodeClick = (node) => {
    setSelectedNode(node);
    setTableData(node.data || []);
  };

  return (
    <div style={{ display: "flex", height: "100%" }}>
      {/* Tree Viewer */}
      <div style={{ flex: 3, borderRight: "1px solid #ccc", padding: "10px" }}>
        <h3>Tree Viewer</h3>
        <ul>
          {treeData.map((node) => (
            <TreeNode key={node.id} node={node} onNodeClick={handleNodeClick} />
          ))}
        </ul>
      </div>

      {/* Table Viewer */}
      <div style={{ flex: 7, padding: "10px" }}>
        <h3>{selectedNode ? selectedNode.label : "Select a Node"}</h3>
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr>
              <th>Column 1</th>
              <th>Column 2</th>
            </tr>
          </thead>
          <tbody>
            {tableData.length > 0 ? (
              tableData.map((row, index) => (
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
      </div>
    </div>
  );
};

// TreeNode Component for Recursive Rendering
const TreeNode = ({ node, onNodeClick }) => {
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <li>
      <div onClick={() => onNodeClick(node)} style={{ cursor: "pointer" }}>
        {node.children && (
          <button onClick={(e) => {
            e.stopPropagation();
            setIsExpanded(!isExpanded);
          }}>
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

export default TreeTableView;
