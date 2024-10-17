package com.cheese.backend.service;

import com.cheese.backend.entity.Cheese;
import com.cheese.backend.repository.CheeseRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class HomeService {

    @Autowired
    CheeseRepository cheeseRepository;

    // Private helper function to convert image data so that front-end can use it directly
    private Cheese getImageData(Cheese cheese) {
        if (cheese.getImageBlob() != null) {
            String base64Image = Base64.getEncoder().encodeToString(cheese.getImageBlob());
            cheese.setImageData("data:" + cheese.getImageType() + ";base64," + base64Image);
        }
        return cheese;
    }

    public Cheese saveCheese(Cheese cheese, MultipartFile imageFile) throws IOException {
        // Save image data
        cheese.setImageName(imageFile.getOriginalFilename());
        cheese.setImageType(imageFile.getContentType());
        cheese.setImageBlob(imageFile.getBytes());

        // Save the cheese and return the created entity
        return cheeseRepository.save(cheese);
    }

    public List<Cheese> getAllCheeses() {
        // Get all cheeses
        List<Cheese> cheeses = cheeseRepository.findAll();
        if (cheeses.isEmpty()) {
            return null;
        }
        // Convert each cheese's imageBlob as imageData
        return cheeses.stream().map(this::getImageData).collect(Collectors.toList());
    }

    public Optional<Cheese> getOneCheese(Long id) {
        return cheeseRepository.findById(id).map(this::getImageData);
    }

    public Cheese updateOneCheese(Long id, Cheese cheese, Optional<MultipartFile> imageFile) throws IOException {
        Optional<Cheese> c = cheeseRepository.findById(id);
        if (c.isEmpty()) {
            return null;
        }

        // Update
        c.get().setName(cheese.getName());
        c.get().setPrice(cheese.getPrice());
        c.get().setColor(cheese.getColor());
        if (imageFile.isPresent()) {
            c.get().setImageName(imageFile.get().getOriginalFilename());
            c.get().setImageType(imageFile.get().getContentType());
            c.get().setImageBlob(imageFile.get().getBytes());
        }
        return cheeseRepository.save(c.get());
    }

    public boolean deleteOneCheese(Long id) {
        if (!cheeseRepository.existsById(id)) {
            return false;
        }
        cheeseRepository.deleteById(id);
        return true;
    }
}
