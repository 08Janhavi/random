public class MyTableView {

    private DataNode comp;
    private List filterPkVal;
    private QueryResult qr;

    public MyTableView(DataNode comp, List filterPkVal) {
        this.comp = comp;
        this.filterPkVal = filterPkVal;
    }

    public void query() {
        try {
            qr = Dao.execQuery(comp.getGridQuery(), filterPkVal);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error executing query: " + ex.getMessage(), ex);
        }
    }

    private void setTableCols() {
        int weight = 100 / comp.getGridColumns().size();

        for (int i = 0; i < comp.getGridColumns().size(); i++) {
            IComp cc = (IComp) comp.getGridColumns().get(i);

            if (cc instanceof Primitive) {
                // Set column metadata if needed
            } else if (cc instanceof DataCombo) {
                // Set column metadata if needed
            }
        }
    }

    private class GenericLabelProvider {

        public String getColumnText(Object obj, int i) {
            List lst = (List) obj;
            String text = "" + lst.get(i);

            if (text.equalsIgnoreCase("false") || text.equalsIgnoreCase("true")) {
                text = "";
            }

            return text;
        }
    }

    private class GenericSorter extends ViewerSorter {
        private int col;
        private boolean reverse;

        public GenericSorter(int col, boolean reverse) {
            super();
            this.col = col;
            this.reverse = reverse;
        }

        public int compare(Object o1, Object o2) {
            int seq = 0;

            Object col1 = ((List) o1).get(col);
            Object col2 = ((List) o2).get(col);

            if (col1 == null) {
                seq = -1;
            } else if (col2 == null) {
                seq = 1;
            } else if (col1 instanceof Comparable) {
                seq = ((Comparable) col1).compareTo(col2);
            }

            return reverse ? -seq : seq;
        }
    }
}
