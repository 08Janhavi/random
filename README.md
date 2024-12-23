const menuOptions = [
  {
    itemId: "1",
    nodeId: "1",
    label: "ESM Dictionary",
    children: [
      { itemId: "1-1", nodeId: "1-1", label: "Vendor Dictionary" },
      {
        itemId: "1-2",
        nodeId: "1-2",
        label: "Product Rule Manager",
        children: [
          { itemId: "1-2-1", nodeId: "1-2-1", label: "Conversion Manager" },
          { itemId: "1-2-2", nodeId: "1-2-2", label: "Categorization Manager" },
        ],
      },
      { itemId: "1-3", nodeId: "1-3", label: "ESM Exception Management" },
    ],
  },
];

function renderTree(items) {
  return items.map((item) => (
    <TreeItem key={item.itemId} itemId={item.itemId} nodeId={item.nodeId} label={item.label}>
      {item.children && renderTree(item.children)}
    </TreeItem>
  ));
}

function EsmManagement() {
  return (
    <div>
      <h3>ESM Management</h3>
      <SimpleTreeView>{renderTree(menuOptions)}</SimpleTreeView>
    </div>
  );
}

export default EsmManagement;
