package com.cheese.backend.HomeControllerTest;

import com.cheese.backend.controller.HomeController;
import com.cheese.backend.entity.Cheese;
import com.cheese.backend.enums.CheeseColor;
import com.cheese.backend.service.HomeService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class HomeControllerTest {

    @Mock
    HomeService homeService;

    @InjectMocks
    HomeController homeController;

    public HomeControllerTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testHealthCheck() {
        HomeController homeController = new HomeController();

        ResponseEntity<Map<String, Boolean>> response = homeController.health();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Boolean> body = response.getBody();
        assert body != null;
        assertEquals(true, body.get("healthy"));
    }

    @Test
    void testAddCheese_Success() throws IOException {
        Cheese cheese = new Cheese();
        cheese.setName("Feta");
        cheese.setPrice((float) 10.99);
        cheese.setColor(CheeseColor.YELLOW);

        MockMultipartFile imageFile = new MockMultipartFile("imageFile",  "cheddar.jpg",  "image/jpeg",
                "image data".getBytes());

        // Mock response
        when(homeService.saveCheese(cheese, imageFile)).thenReturn(cheese);

        ResponseEntity<?> response = homeController.addCheese(cheese, imageFile);

        // Success
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Cheese added successfully", response.getBody());
    }

    @Test
    void testAddCheese_InvalidInput() {
        ResponseEntity<?> response = homeController.addCheese(null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("You must provide a valid request", response.getBody());
    }

    @Test
    public void testGetCheeses_Success() {
        List<Cheese> cheeseList = new ArrayList<>();
        Cheese cheese1 = new Cheese();
        cheese1.setName("Feta");
        cheese1.setPrice((float) 10.99);
        cheese1.setColor(CheeseColor.YELLOW);
        cheeseList.add(cheese1);

        Cheese cheese2 = new Cheese();
        cheese2.setName("Parmesan");
        cheese2.setPrice((float) 15.99);
        cheese2.setColor(CheeseColor.WHITE);
        cheeseList.add(cheese2);

        when(homeService.getAllCheeses()).thenReturn(cheeseList);

        ResponseEntity<List<Cheese>> response = homeController.getCheeses();

        // Success
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cheeseList.size(), response.getBody().size());
    }

    @Test
    public void testGetCheeses_NoCheese() {
        // No cheese
        List<Cheese> cheeseList = new ArrayList<>();

        when(homeService.getAllCheeses()).thenReturn(cheeseList);

        ResponseEntity<List<Cheese>> response = homeController.getCheeses();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertEquals(0, cheeseList.size());
    }

    @Test
    public void testGetOneCheese_Success() {
        Cheese cheese1 = new Cheese();
        cheese1.setName("Feta");
        cheese1.setPrice((float) 10.99);
        cheese1.setColor(CheeseColor.YELLOW);

        when(homeService.getOneCheese(1L)).thenReturn(Optional.of(cheese1));

        ResponseEntity<?> response = homeController.getOneCheese(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Optional<Cheese> c = (Optional<Cheese>) response.getBody();
        assertEquals(cheese1.getName(), c.get().getName());
        assertEquals(cheese1.getPrice(), c.get().getPrice());
        assertEquals(cheese1.getColor(), c.get().getColor());
    }

    @Test
    public void testGetOneCheese_NoCheese() {
        // No cheese ID 1
        when(homeService.getOneCheese(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = homeController.getOneCheese(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testUpdateCheese_Success() throws IOException {
        Cheese cheese = new Cheese();
        cheese.setName("Feta");
        cheese.setPrice((float) 10.99);
        cheese.setColor(CheeseColor.YELLOW);
        Optional<MultipartFile> imageFile = Optional.empty();

        when(homeService.updateOneCheese(1L, cheese, imageFile)).thenReturn(cheese);

        ResponseEntity<?> response = homeController.updateCheese(1L, cheese, imageFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cheese updated successfully", response.getBody());
    }

    @Test
    public void testUpdateCheese_NoCheese() throws IOException {
        ResponseEntity<?> response = homeController.updateCheese(1L, null, Optional.empty());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("You must provide a valid request", response.getBody());
    }

    @Test
    public void testDeleteCheese_Success() {
        when(homeService.deleteOneCheese(1L)).thenReturn(true);

        ResponseEntity<?> response = homeController.deleteCheese(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cheese deleted successfully", response.getBody());
    }

    @Test
    public void testDeleteCheese_NoContent() {
        when(homeService.deleteOneCheese(1L)).thenReturn(false);

        ResponseEntity<?> response = homeController.deleteCheese(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
