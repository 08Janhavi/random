@RestController
@RequestMapping
@CrossOrigin(origins="http://localhost:5173")
public class LineageDataController {

    @Autowired
    private IMLineageDataDAO lineageDataDAO;

    private static final Logger logger = LogManager.getLogger(LineageDataController.class.getName());

    @GetMapping("/getDatabases")
    public List<String> getDatabases(){
        try{
            List<String> databases=lineageDataDAO.getAllDatabases();
            return databases;
        }
        catch(Exception e) {
            logger.error("Error fetching databases",e);
            return new ArrayList<>();
        }
    }

    @GetMapping("/getTables")
    public List<String> getTables(){
        try{
            List<String> tables=lineageDataDAO.getAllTables();
            return tables;
        }
        catch(Exception e) {
            logger.error("Error fetching databases",e);
            return new ArrayList<>();
        }
    }


    @GetMapping("/getColumnMappings")
    public List<Table> getColumnMappings(@RequestParam("db") String db, @RequestParam("table") String table){
        if(db==null || table==null) return new ArrayList<>();
        logger.info("data printed");
        return lineageDataDAO.getLineageDataFromDB(db,table);
    }


    @PostMapping("/saveColumnMappings")
    public ResponseEntity<String> saveColumnMappings(@RequestBody Table table) {
        System.out.println("reached inside");
        logger.info("hey im here");
        if (table == null) return ResponseEntity.badRequest().body("Invalid data");

        logger.info("updating lineage data " );
        lineageDataDAO.saveLineageData(table);

        return ResponseEntity.ok("Data saved successfully");
    }


    @DeleteMapping("/deleteFileColumn")
    public ResponseEntity<String> deleteFileColumn(@RequestBody Table table) {
        logger.info("Received request to delete file column for table: {}", table.tableName());
        System.out.println(table);

        // Check if table or file columns are null
        if (table == null || table.tableColumns() == null || table.tableColumns().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid data");
        }

        try {
            lineageDataDAO.deleteFileColumn(table);
            return ResponseEntity.ok("File column deleted successfully");
        } catch (Exception e) {
            logger.error("Error occurred while deleting file column: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete file column");
        }
    }

}
