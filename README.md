public class TableMenuManager {

    public static int COPY_ID = 0;
    public static int PASTE_ID = 1;
    public static int ADD_ID = 2;
    public static int EDIT_ID = 3;
    public static int DELETE_ID = 4;

    private CopytoClipboardAction copytoClipboardAction;
    private EditAction editAction;
    private AddAction insertAction;
    private DeleteAction deleteAction;
    private PasteAction pasteAction;
    private CopyAction copyAction;
    private SelectAllAction selectAllAction;

    public TableMenuManager() { }

    public void manageActions(ModelTableView modelTableView, GuiInfo guiinfo, ProcessInfo processInfo, RequestReferences rr, Object obj) {
        int popupType = guiinfo.getPopupMenuType();

        boolean showCopyToClipboard = true;
        boolean showAdd = true;
        boolean showEdit = true;
        boolean showDelete = true;
        boolean showCopy = true;
        boolean showPaste = false;

        if (popupType == GuiInfo.ALL_NO_INSERT_MENU) {
            showAdd = false;
        } else if (popupType == GuiInfo.UPDATE_ONLY_MENU) {
            showAdd = false;
            showDelete = false;
            showCopyToClipboard = false;
        } else if (popupType == GuiInfo.NONE_MENU) {
            return;
        }

        copytoClipboardAction = new CopytoClipboardAction();
        editAction = new EditAction(processInfo, rr);
        insertAction = new AddAction(processInfo, rr, obj);
        deleteAction = new DeleteAction(processInfo, rr);
        pasteAction = new PasteAction(processInfo, rr);
        copyAction = new CopyAction(processInfo);

        copytoClipboardAction.setEnabled(showCopyToClipboard);
        deleteAction.setEnabled(showDelete);
        insertAction.setEnabled(showAdd);
        editAction.setEnabled(showEdit);
        pasteAction.setEnabled(showPaste);
        copyAction.setEnabled(showCopy);
    }
}
