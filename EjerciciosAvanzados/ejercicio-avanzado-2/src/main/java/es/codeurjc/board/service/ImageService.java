package es.codeurjc.board.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "uploads");

    public String createImage(InputStream inputStream, String originalFilename) throws IOException {

        validateExtension(originalFilename);

        String filename = "image_" + UUID.randomUUID() + "." + getExtension(originalFilename);

        Files.createDirectories(IMAGES_FOLDER);
        
        // Delete existing file if it exists to avoid conflicts
        Path filePath = IMAGES_FOLDER.resolve(filename);
        Files.deleteIfExists(filePath);
        
        Files.copy(inputStream, filePath);

        return filename;
    }

    public Resource getImageFile(String filename) throws IOException {

        Path file = IMAGES_FOLDER.resolve(filename);
        Resource resource = new UrlResource(file.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("Image file not found");
        }

        return resource;
    }

    public void deleteImage(String filename) throws IOException {
        if (filename != null) {
            Files.deleteIfExists(IMAGES_FOLDER.resolve(filename));
        }
    }

    public String getContentType(String filename) {
        String extension = "";
        if (filename != null && filename.contains(".")) {
            extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        }
        
        return switch (extension) {
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            default -> "application/octet-stream";
        };
    }

    private static final java.util.Set<String> ALLOWED_EXTENSIONS =
            java.util.Set.of("png", "jpg", "jpeg");

    private void validateExtension(String filename) {
        String ext = getExtension(filename);
        if (!ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Invalid image format. Only .png, .jpg and .jpeg are allowed.");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "bin";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
