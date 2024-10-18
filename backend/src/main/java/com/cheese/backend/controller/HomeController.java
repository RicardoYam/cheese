package com.cheese.backend.controller;

import com.cheese.backend.entity.Cheese;
import com.cheese.backend.enums.CheeseColor;
import com.cheese.backend.service.HomeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@Log4j2
public class HomeController {

    @Autowired
    HomeService homeService;


    /**
     * Checks the health status of the application.
     * <p>
     * This method returns a simple JSON response indicating whether the application
     * is healthy. If any errors occur during the execution, it will return an
     * internal server error status.
     *
     * @return A ResponseEntity containing a map with a key "healthy" and
     *         a value of true if the application is running correctly.
     *         If an error occurs, it returns a HttpStatus response.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Boolean>> health() {
        try {
            // response "healthy: true"
            Map<String, Boolean> response = new HashMap<>();
            response.put("healthy", true);
            return ResponseEntity.ok(response); 
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Adds a new cheese to the database along with its image.
     * <p>
     * This method handles the HTTP POST request to create a new cheese.
     * It validates the incoming cheese object and its associated image file,
     * ensuring that all required fields are present and valid.
     *
     * TODO: Images can be uploaded to AWS S3 for the high reliability(cross region) and cost efficiency(S3
     * life-cycle). Because of the configuration process and limited time, I just leave S3 there :)
     *
     * TODO: Image data can be stored in a separate table and linked to the cheese table using a foreign key. This
     * way, we can join the tables to get the list of images for each cheese. Because this is a quick demo, i will
     * just store one image per cheese.
     *
     * @param cheese The Cheese object containing the details of the cheese to be added.
     *               Must include a valid name, price greater than zero, and a valid color.
     * @param imageFile The image file associated with the cheese.
     *                  This file must not be null or empty.
     * @return ResponseEntity indicating the result of the operation.
     *         Returns a BAD_REQUEST status if validation fails,
     *         and a CREATED status if the cheese is successfully added.
     *         In case of an IOException, an INTERNAL_SERVER_ERROR status is returned.
     */
    @PostMapping("/cheeses")
    public ResponseEntity<?> addCheese(@RequestPart Cheese cheese, @RequestPart MultipartFile imageFile) {
        // Check is request body exist
        if (cheese == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must provide a valid request");
        }

        // Check empty name
        if (cheese.getName() == null || cheese.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cheese name required");
        }

        // Check valid cheese price
        if (cheese.getPrice() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cheese price must greater than 0");
        }

        // Check valid cheese color
        if (cheese.getColor() == null || !CheeseColor.isValidColor(cheese.getColor().toString())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cheese color is invalid");
        }

        // Check valid image file
        if (imageFile == null || imageFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cheese image is required");
        }

        try {
            // Call the service to process the logic part
            homeService.saveCheese(cheese, imageFile);
            return ResponseEntity.status(HttpStatus.CREATED).body("Cheese added successfully");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Retrieves a list of all cheeses from the database.
     * <p>
     * This method handles the HTTP GET request to fetch all cheeses.
     * It returns a response containing the list of cheeses or indicates
     * that no content is available if the list is empty.
     *
     * @return ResponseEntity<List<Cheese>> containing the list of cheeses.
     *         Returns an OK status with the list of cheeses if available,
     *         or a NO_CONTENT status if no cheeses are found.
     */
    @GetMapping("/cheeses")
    public ResponseEntity<List<Cheese>> getCheeses() {
        List<Cheese> cheeses = homeService.getAllCheeses();
        if (cheeses == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cheeses);
    }


    /**
     * Retrieves a cheese by its ID.
     * <p>
     * This method handles the HTTP GET request to fetch a specific cheese
     * based on the provided ID. If the cheese is found, it returns the cheese
     * object; otherwise, it returns a NO_CONTENT status.
     *
     * @param id The ID of the cheese to retrieve.
     * @return ResponseEntity<?> containing the cheese if found,
     *         or a NO_CONTENT status if the cheese with the specified ID does not exist.
     */
    @GetMapping("/cheeses/{id}")
    public ResponseEntity<?> updateCheese(@PathVariable("id") Long id) {
        Optional<Cheese> cheese = homeService.getOneCheese(id);
        if (cheese.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cheese);
    }


    /**
     * Updates an existing cheese by its ID.
     * <p>
     * This method handles the HTTP PUT request to update the details of a specific cheese,
     * including its name, price, color, and optionally its image. It validates the provided
     * cheese object and returns appropriate HTTP status codes.
     *
     * @param id The ID of the cheese to update.
     * @param cheese The cheese object containing the updated details. Must not be null.
     * @param imageFile An optional MultipartFile containing the updated cheese image.
     * @return ResponseEntity<?> indicating the result of the update operation.
     *         Returns HTTP 200 (OK) if the cheese is successfully updated,
     *         HTTP 204 (NO CONTENT) if the cheese does not exist,
     *         or HTTP 400 (BAD REQUEST) if validation fails.
     * @throws IOException If an error occurs while processing the image file.
     */
    @PutMapping("cheeses/{id}")
    public ResponseEntity<?> updateCheese(@PathVariable("id") Long id, @RequestPart Cheese cheese,
                                          @RequestPart Optional<MultipartFile> imageFile) throws IOException {
        if (cheese == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must provide a valid request");
        }

        if (cheese.getPrice() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cheese price must greater than 0");
        }

        // Check valid cheese color
        if (cheese.getColor() == null || !CheeseColor.isValidColor(cheese.getColor().toString())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cheese color is invalid");
        }

        try {
            if (homeService.updateOneCheese(id, cheese, imageFile) != null) {
                return ResponseEntity.status(HttpStatus.OK).body("Cheese updated successfully");
            }
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Deletes a cheese by its ID.
     * <p>
     * This method handles the HTTP DELETE request to remove a specific cheese from the database.
     * It returns appropriate HTTP status codes.
     *
     * @param id The ID of the cheese to delete.
     * @return ResponseEntity<?> indicating the result of the delete operation.
     *         Returns HTTP 200 (OK) if the cheese is successfully deleted,
     *         HTTP 204 (NO CONTENT) if the cheese does not exist,
     *         or HTTP 500 (INTERNAL SERVER ERROR) if an unexpected error occurs.
     */
    @DeleteMapping("/cheeses/{id}")
    public ResponseEntity<?> deleteCheese(@PathVariable("id") Long id) {
        try {
            if (homeService.deleteOneCheese(id)) {
                return ResponseEntity.status(HttpStatus.OK).body("Cheese deleted successfully");
            }
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}