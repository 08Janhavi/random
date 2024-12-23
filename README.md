@PostMapping("/navigationView")
    public ResponseEntity<String> handleNavigationView() {
        // Simulate some backend processing
        System.out.println("Navigation view service called");

        // You can add any logic here, like updating a database or logging
        return new ResponseEntity<>("Service called successfully", HttpStatus.OK);
    }
