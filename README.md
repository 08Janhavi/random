import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { SimpleTreeView } from "@mui/x-tree-view/SimpleTreeView";
import { TreeItem } from "@mui/x-tree-view/TreeItem";
import axios from "axios";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import "./EsmManagement.css";

function EsmManagement() {
  const [selectedOption, setSelectedOption] = useState("ESM Dictionary");
  const [responseMessage, setResponseMessage] = useState("");
  const navigate = useNavigate();
  const location = useLocation();
  const { enableAuditButton } = location.state || { enableAuditButton: false };

  const menuOptions = [
    // Define menu options as in your code
  ];

  useEffect(() => {
    const fetchNavigationData = async () => {
      try {
        await axios.post("http://localhost:8080/api/navigationview");
        console.log("Backend service called successfully.");
      } catch (error) {
        console.error("Error fetching navigation data", error);
      }
    };
    fetchNavigationData();
  }, []);

  const handleMenuClick = async (optionLabel) => {
    setSelectedOption(optionLabel);
    try {
      const response = await axios.post("http://localhost:8080/api/handleSelection", optionLabel, {
        headers: {
          "Content-Type": "text/plain",
        },
      });
      setResponseMessage(response.data);
    } catch (error) {
      console.error("Error handling menu selection", error);
      setResponseMessage("Error processing the selected option.");
    }
  };

  const renderTree = (items) =>
    items.map((item) => (
      <TreeItem
        key={item.itemId}
        nodeId={item.nodeId}
        label={item.label}
        onClick={() => handleMenuClick(item.label)}
      >
        {item.children && renderTree(item.children)}
      </TreeItem>
    ));

  return (
    <div style={{ fontFamily: "Arial", height: "100vh", display: "flex", flexDirection: "column" }}>
      {/* Top Tab */}
      <div style={{ display: "flex", justifyContent: "space-between", padding: "16px 20px", backgroundColor: "rgb(243, 243, 243)" }}>
        <h3>ESM Management</h3>
        <div>
          <button
            style={{ marginRight: "10px", padding: "5px 10px", border: "1px solid #ccc", borderRadius: "6px" }}
            onClick={() => navigate("/")}
          >
            DB Login
          </button>
          <button
            style={{ padding: "5px 10px", border: "1px solid #ccc", borderRadius: "6px" }}
            onClick={() => setShowAuditDropdown((prev) => !prev)}
            disabled={!enableAuditButton}
          >
            Audit
          </button>
        </div>
      </div>

      {/* Main Layout */}
      <div style={{ display: "flex", flex: 1 }}>
        {/* Sidebar */}
        <div style={{ width: "250px", padding: "10px", background: "#f9f9f9", borderRight: "1px solid #ccc" }}>
          <h4>Menu</h4>
          <SimpleTreeView
            defaultCollapseIcon={<ExpandMoreIcon />}
            defaultExpandIcon={<ChevronRightIcon />}
          >
            {renderTree(menuOptions)}
          </SimpleTreeView>
        </div>

        {/* Content Area */}
        <div style={{ flex: 1, padding: "10px" }}>
          <h3>{selectedOption}</h3>
          <div style={{ height: "calc(100% - 50px)", overflow: "auto" }}>
            <p>Content for {selectedOption} will be displayed here.</p>
            <p style={{ color: "green" }}>{responseMessage}</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default EsmManagement;








@RestController
@RequestMapping("/api")
public class NavigationController {

    @PostMapping("/handleSelection")
    public ResponseEntity<String> handleSelection(@RequestBody String selectedOption) {
        try {
            // Log the selected option
            System.out.println("Handling selection for: " + selectedOption);

            // Simulated structured selection processing
            String responseMessage;

            // Mocking a tree node model and process manager for demonstration
            switch (selectedOption) {
                case "ESM Dictionary Manager": {
                    // Create a model and request references
                    Model model = new Model("ESM Dictionary Manager", "TypeA");
                    RequestReferences requestReferences = new RequestReferences();
                    ProcessInfo processInfo = new ProcessInfo();
                    ProcessManager manager = new ProcessManager(processInfo, requestReferences);

                    // Process selection
                    System.out.println("Processing ESM Dictionary Manager selection...");
                    manager.processSelection(model);
                    refreshTable(requestReferences, processInfo);

                    responseMessage = "Processed ESM Dictionary Manager successfully.";
                    break;
                }
                case "Calendars": {
                    Model model = new Model("Calendars", "TypeB");
                    RequestReferences requestReferences = new RequestReferences();
                    ProcessInfo processInfo = new ProcessInfo();
                    ProcessManager manager = new ProcessManager(processInfo, requestReferences);

                    // Process selection
                    System.out.println("Processing Calendars selection...");
                    manager.processSelection(model);
                    refreshTable(requestReferences, processInfo);

                    responseMessage = "Processed Calendars successfully.";
                    break;
                }
                case "Map Rule": {
                    Model model = new Model("Map Rule", "TypeC");
                    RequestReferences requestReferences = new RequestReferences();
                    ProcessInfo processInfo = new ProcessInfo();
                    ProcessManager manager = new ProcessManager(processInfo, requestReferences);

                    // Process selection
                    System.out.println("Processing Map Rule selection...");
                    manager.processSelection(model);
                    refreshTable(requestReferences, processInfo);

                    responseMessage = "Processed Map Rule successfully.";
                    break;
                }
                case "Conversion Layout": {
                    Model model = new Model("Conversion Layout", "TypeD");
                    RequestReferences requestReferences = new RequestReferences();
                    ProcessInfo processInfo = new ProcessInfo();
                    ProcessManager manager = new ProcessManager(processInfo, requestReferences);

                    // Process selection
                    System.out.println("Processing Conversion Layout selection...");
                    manager.processSelection(model);
                    refreshTable(requestReferences, processInfo);

                    responseMessage = "Processed Conversion Layout successfully.";
                    break;
                }
                default:
                    responseMessage = "Processed generic menu item.";
                    break;
            }

            return ResponseEntity.ok(responseMessage);

        } catch (Exception e) {
            // Handle exceptions and return error response
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error processing selection: " + e.getMessage());
        }
    }

    private void refreshTable(RequestReferences requestReferences, ProcessInfo processInfo) {
        // Simulate refreshing table based on the process info
        System.out.println("Refreshing table with process info...");
        // Add your actual refresh logic here
    }
}

// Mock classes for the process
class Model {
    private String action;
    private String type;

    public Model(String action, String type) {
        this.action = action;
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public String getType() {
        return type;
    }
}

class RequestReferences {
    // Add necessary fields and methods
}

class ProcessInfo {
    // Add necessary fields and methods
}

class ProcessManager {
    private ProcessInfo processInfo;
    private RequestReferences requestReferences;

    public ProcessManager(ProcessInfo processInfo, RequestReferences requestReferences) {
        this.processInfo = processInfo;
        this.requestReferences = requestReferences;
    }

    public void processSelection(Model model) {
        System.out.println("Processing model: " + model.getAction() + " with type: " + model.getType());
    }
}
