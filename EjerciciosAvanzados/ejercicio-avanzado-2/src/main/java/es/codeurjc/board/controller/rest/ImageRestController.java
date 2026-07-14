package es.codeurjc.board.controller.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.board.service.ImageService;

@RestController
public class ImageRestController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/api/images")
    public ResponseEntity<Resource> getImage(@RequestParam String filename) throws IOException {

        Resource file = imageService.getImageFile(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, imageService.getContentType(filename))
                .body(file);
    }

}
