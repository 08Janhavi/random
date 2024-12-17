import java.util.Iterator;
import java.util.logging.Logger;

public class NavigationService {

    private static final Logger log = Logger.getLogger(NavigationService.class.getName());

    private String connectionString;

    public NavigationService(String connectionString) {
        this.connectionString = connectionString;
        log.info("Constructing navigation service with connectionString=" + connectionString);
    }

    public void processData(RequestReferences requestReferences, ProcessInfo processInfo) {
        log.info("Processing data: requestReferences=" + requestReferences + ", processInfo=" + processInfo);

        String activeListName = (String) requestReferences.get(IRequestReferences.ACTIVE_LIST_NAME_KEYWORD);
        if (activeListName == null) {
            log.warning("No active list name found. Default processing...");
            return;
        }

        Object data = requestReferences.get(activeListName);
        if (data == null) {
            log.warning("No data found for the active list name.");
            return;
        }

        int type = processInfo.getType();
        String title = ComponentMapping.getTitle(type);

        log.info("Title=" + title + ", Type=" + type);
        try {
            GuiInfo guiInfo = new GuiInfo(type);
            guiInfo.setPopupMenuType(ComponentMapping.getPopupType(type).intValue());

            String listPanelName = ComponentMapping.getListPanelName(type);
            ModelTableView tableView = (ModelTableView) Class.forName(listPanelName).getDeclaredConstructor().newInstance();

            String[] colNames = ComponentMapping.getListHeader(type);
            tableView.setTable(colNames, guiInfo, requestReferences, processInfo);
            tableView.display();

        } catch (Exception e) {
            log.severe("Error processing data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleSelection(Model model) {
        log.info("Handling tree node selection: " + model.getAction());
        RequestReferences requestReferences = new RequestReferences();
        ProcessInfo processInfo = new ProcessInfo();
        ProcessManager manager = new ProcessManager(processInfo, requestReferences);

        try {
            if (model instanceof MyCategorization) {
                MyCategorization myCat = (MyCategorization) model;
                if (myCat.getComp() instanceof StaticNode) {
                    log.info("Processing StaticNode");
                    MyStaticTableView staticTableView = new MyStaticTableView((StaticNode) myCat.getComp());
                    staticTableView.query();

                } else if (myCat.getComp() instanceof DataNode) {
                    log.info("Processing DataNode");
                    MyTableView dataTableView = new MyTableView((DataNode) myCat.getComp(), myCat.getPkVal());
                    dataTableView.query();
                }
            } else {
                log.info("Refreshing table for non-categorization model.");
                manager.processSelection(model);
                processData(requestReferences, processInfo);
            }
        } catch (Exception e) {
            log.severe("Error handling selection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getConnectionString() {
        return connectionString;
    }
}
