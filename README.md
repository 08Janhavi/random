@RestController
@RequestMapping("/api/lineage")
@CrossOrigin(origins = "http://localhost:5173")
public class LineageDataController {

    @Autowired
    private IMLineageDataDAO lineageDataDAO;

    private static final Logger logger = LogManager.getLogger(LineageDataController.class.getName());

    // Existing method for fetching data
    @GetMapping("/getColumnMappings")
    public List<Table> getColumnMappings(@RequestParam("db") String db, @RequestParam("table") String table) {
        if (db == null || table == null) {
            return new ArrayList<>();
        }
        logger.info("Fetching data for DB: {} and Table: {}", db, table);
        return lineageDataDAO.getLineageDataFromDB(db, table);
    }

    // New method for saving or updating data
    @PostMapping("/saveColumnMappings")
    public ResponseEntity<String> saveColumnMappings(@RequestBody List<Table> updatedTables) {
        try {
            logger.info("Received updated data for saving: {}", updatedTables);
            lineageDataDAO.saveOrUpdateLineageData(updatedTables);
            return ResponseEntity.ok("Data saved successfully");
        } catch (Exception e) {
            logger.error("Error saving data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving data");
        }
    }
}











