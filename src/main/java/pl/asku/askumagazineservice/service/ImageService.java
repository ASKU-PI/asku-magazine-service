package pl.asku.askumagazineservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.asku.askumagazineservice.model.Image;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.repository.ImageRepository;
import pl.asku.askumagazineservice.repository.MagazineRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
@AllArgsConstructor
public class ImageService {

    ImageRepository imageRepository;
    MagazineRepository magazineRepository;

    public Image addImage(MultipartFile file, Long magazineId, String username, String format) throws IOException {
        Optional<Magazine> magazine = magazineRepository.findById(magazineId);
        if(magazine.isEmpty() || !magazine.get().getOwner().equals(username) ||
                (!format.equals("jpg") && !format.equals("png"))){
            return null;
        }
        Image image = Image.builder()
                .magazine(magazine.get())
                .picByte(compressBytes(file.getBytes()))
                .format(format)
                .build();
        return imageRepository.save(image);
    }

    public static byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException ignored) {
        }
        System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    public static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException ignored) {
        }
        return outputStream.toByteArray();
    }

    public Image getImageById(Long id, String extension){
        Optional<Image> retrievedImage = imageRepository.findById(id);
        if(retrievedImage.isEmpty()
                || !extension.equals(retrievedImage.get().getFormat())) return null;
        retrievedImage.get().setPicByte(
                decompressBytes(retrievedImage.get().getPicByte()));
        return retrievedImage.get();
    }

}
