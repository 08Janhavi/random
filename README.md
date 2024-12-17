import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Iterator;
import java.util.List;

public class CopytoClipboardAction {

    private List<DataObject> dataObjects;

    public CopytoClipboardAction(List<DataObject> dataObjects) {
        this.dataObjects = dataObjects;
    }

    public void run() {
        StringBuffer copy = new StringBuffer();

        try {
            for (DataObject obj : dataObjects) {
                if (obj != null) {
                    ObjectEditView v = new ObjectEditView(obj);
                    StringBuffer buff = new StringBuffer();
                    for (int findex = 0; findex < v.size(); findex++) {
                        if (findex > 0) buff.append('\t');
                        buff.append(v.getValue(findex));
                    }
                    buff.append('\n');
                    copy.append(buff.toString());
                }
            }

            StringSelection stringSelection = new StringSelection(copy.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

        } catch (Exception e) {
            // Handle exception as per your backend requirements
            e.printStackTrace();
        }
    }
}
