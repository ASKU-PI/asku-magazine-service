package pl.asku.askumagazineservice.controller;

import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.asku.askumagazineservice.model.Image;
import pl.asku.askumagazineservice.service.ImageService;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/image")
@AllArgsConstructor
public class ImageController {

    ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity uploadImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("magazineId") Long magazineId,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();
            Arrays.asList(files).forEach(file -> {
                try {
                    String extension = file.getOriginalFilename().split("\\.")[1];
                    imageService.addImage(file, magazineId, username, extension);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Images uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body("Upload failed");
        }
    }

    @GetMapping(path = { "/get/{id}.{extension}" })
    public void getImage(
            @PathVariable("id") Long id,
            @PathVariable("extension") String extension,
            HttpServletResponse response) throws IOException {
        Image image = imageService.getImageById(id, extension);
        InputStream in = new ByteArrayInputStream(image.getPicByte());
        switch (image.getFormat()){
            case "png":
                response.setContentType(MediaType.IMAGE_PNG_VALUE);
                break;
            case "jpg":
                response.setContentType(MediaType.IMAGE_JPEG_VALUE);
                break;
        }
        IOUtils.copy(in, response.getOutputStream());
    }
}
