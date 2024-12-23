import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { TreeView, TreeItem } from "@mui/lab";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import "./EsmManagement.css";

function EsmManagement() {
  const [selectedOption, setSelectedOption] = useState("ESM Dictionary");
  const [showAuditDropdown, setShowAuditDropdown] = useState(false);

  const navigate = useNavigate();
  const location = useLocation();

  const { enableAuditButton } = location.state || { enableAuditButton: false };

  const handleAuditOptionOne = () => {
    setShowAuditDropdown(false);
    navigate("/viewMyAudits");
  };

  const handleAuditOptionTwo = () => {
    setShowAuditDropdown(false);
    navigate("/executeSQL");
  };

  const handleLogin = () => {
    navigate("/");
  };

  const handleMenuClick = (option) => {
    setSelectedOption(option);
  };

  const menuOptions = [
    {
      id: "1",
      label: "ESM Dictionary Manager",
      children: [
        { id: "1-1", label: "Vendor Dictionary" },
        { id: "1-2", label: "Conversion Manager" },
      ],
    },
    {
      id: "2",
      label: "Product Rule Manager",
    },
    {
      id: "3",
      label: "Categorization Manager",
    },
    {
      id: "4",
      label: "ESM Exception Management",
    },
  ];

  return (
    <div
      style={{
        fontFamily: "Arial",
        height: "100vh",
        display: "flex",
        flexDirection: "column",
      }}
    >
      {/* Top Tab with Buttons */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          padding: "16px 20px",
          backgroundColor: "rgb(243, 243, 243)",
          color: "#000",
          position: "relative",
        }}
      >
        <h3 style={{ margin: 0 }}>ESM Management</h3>
        <div>
          <button
            style={{
              marginRight: "10px",
              padding: "5px 10px",
              backgroundColor: "#fff",
              border: "1px solid #ccc",
              borderRadius: "6px",
              cursor: "pointer",
            }}
            onClick={handleLogin}
          >
            DB Login
          </button>
          <div style={{ position: "relative", display: "inline-block" }}>
            <button
              style={{
                padding: "5px 10px",
                backgroundColor: "#fff",
                border: "1px solid #ccc",
                borderRadius: "4px",
                cursor: "pointer",
              }}
              onClick={() => setShowAuditDropdown((prev) => !prev)}
              disabled={!enableAuditButton}
            >
              Audit
            </button>
            {showAuditDropdown && (
              <div
                style={{
                  position: "absolute",
                  top: "40px",
                  right: 0,
                  backgroundColor: "#fff",
                  border: "1px solid #ccc",
                  borderRadius: "4px",
                  boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.1)",
                  zIndex: 1000,
                  minWidth: "150px",
                }}
              >
                <ul
                  style={{
                    listStyle: "none",
                    padding: "10px",
                    margin: 0,
                  }}
                >
                  <li
                    style={{
                      padding: "8px",
                      cursor: "pointer",
                      backgroundColor: "#fff",
                      color: "#000",
                      borderBottom: "1px solid #eee",
                    }}
                    onClick={handleAuditOptionOne}
                  >
                    View My Audits
                  </li>
                  <li
                    style={{
                      padding: "8px",
                      cursor: "pointer",
                      backgroundColor: "#fff",
                      color: "#000",
                    }}
                    onClick={handleAuditOptionTwo}
                  >
                    Execute SQL
                  </li>
                </ul>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Main Layout */}
      <div style={{ display: "flex", flex: 1 }}>
        {/* Sidebar */}
        <div
          style={{
            width: "250px",
            borderRight: "1px solid #ccc",
            padding: "10px",
            background: "#f9f9f9",
          }}
        >
          <h4 style={{ marginBottom: "10px" }}>Menu</h4>
          <TreeView
            defaultCollapseIcon={<ExpandMoreIcon />}
            defaultExpandIcon={<ChevronRightIcon />}
          >
            {menuOptions.map((menu) => (
              <TreeItem
                key={menu.id}
                nodeId={menu.id}
                label={menu.label}
                onClick={() => handleMenuClick(menu.label)}
              >
                {menu.children &&
                  menu.children.map((child) => (
                    <TreeItem
                      key={child.id}
                      nodeId={child.id}
                      label={child.label}
                      onClick={() => handleMenuClick(child.label)}
                    />
                  ))}
              </TreeItem>
            ))}
          </TreeView>
        </div>

        {/* Content Area */}
        <div style={{ flex: 1, padding: "10px" }}>
          <h3>{selectedOption}</h3>
          <div
            style={{
              border: "1px solid #ccc",
              height: "calc(100% - 50px)",
              background: "#fff",
              padding: "10px",
              overflow: "auto",
              marginTop: "10px",
            }}
          >
            <p>Content for {selectedOption} will be displayed here.</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default EsmManagement;
