package pl.asku.askumagazineservice.controller;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.dto.MagazinePreviewDto;
import pl.asku.askumagazineservice.dto.imageservice.MagazinePictureDto;
import pl.asku.askumagazineservice.model.*;
import pl.asku.askumagazineservice.security.policy.MagazinePolicy;
import pl.asku.askumagazineservice.service.MagazineService;

import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class MagazineController {

    private final MagazineService magazineService;
    private final MagazinePolicy magazinePolicy;
    private final ImageServiceClient imageServiceClient;

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<MagazineDto> addMagazine(
            @ModelAttribute Magazine magazine,
            @RequestPart("files") MultipartFile[] photos,
            Authentication authentication){
        if(!magazinePolicy.addMagazine(authentication))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(magazine.toMagazineDto());

        String identifier = authentication.getName();

        MagazinePictureDto magazinePictureDto;

        try {
            magazine = magazineService.addMagazine(magazine.toMagazineDto(), identifier);
            magazinePictureDto = imageServiceClient.uploadMagazinePictures(magazine.getId(), photos);
        } catch (ValidationException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        MagazineDto magazineDto = magazine.toMagazineDto();
        magazineDto.setId(magazine.getId());
        magazineDto.setPhotos(magazinePictureDto.getPhotos());

        return ResponseEntity.status(HttpStatus.CREATED).body(magazineDto);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<MagazineDto> getMagazineDetails(@PathVariable Long id){
        Optional<Magazine> magazine = magazineService.getMagazineDetails(id);
        if(magazine.isPresent()){
            MagazinePictureDto magazinePictureDto = imageServiceClient.getMagazinePictures(magazine.get().getId());
            MagazineDto magazineDto = magazine.get().toMagazineDto();
            magazineDto.setPhotos(magazinePictureDto.getPhotos());
            return ResponseEntity.status(HttpStatus.OK).body(magazineDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/user/{identifier}")
    public ResponseEntity<List<MagazinePreviewDto>> getUserMagazines(
            @PathVariable String identifier,
            @RequestParam Optional<Integer> page
    ) {
        List<Magazine> magazines =
                magazineService.getUserMagazines(identifier, page.map(integer -> integer - 1).orElse(0));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(magazines.stream().map(magazine -> {
                    MagazinePictureDto magazinePictureDto = imageServiceClient.getMagazinePictures(magazine.getId());
                    MagazinePreviewDto magazinePreviewDto = magazine.toMagazinePreviewDto();
                    magazinePreviewDto.setPhotos(magazinePictureDto.getPhotos());
                    return magazinePreviewDto;

                }).collect(Collectors.toList()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MagazinePreviewDto>> searchMagazines(
            @RequestParam Optional<Integer> page,
            @RequestParam String location,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam BigDecimal minArea,
            @RequestParam(required = false) Optional<BigDecimal> pricePerMeter,
            @RequestParam(required = false) Optional<MagazineType> type,
            @RequestParam(required = false) Optional<Heating> heating,
            @RequestParam(required = false) Optional<Light> light,
            @RequestParam(required = false) Optional<Boolean> whole,
            @RequestParam(required = false) Optional<Boolean> monitoring,
            @RequestParam(required = false) Optional<Boolean> antiTheftDoors,
            @RequestParam(required = false) Optional<Boolean> ventilation,
            @RequestParam(required = false) Optional<Boolean> smokeDetectors,
            @RequestParam(required = false) Optional<Boolean> selfService,
            @RequestParam(required = false) Optional<Boolean> floor,
            @RequestParam(required = false) Optional<BigDecimal> height,
            @RequestParam(required = false) Optional<BigDecimal> doorHeight,
            @RequestParam(required = false) Optional<BigDecimal> doorWidth,
            @RequestParam(required = false) Optional<Boolean> electricity,
            @RequestParam(required = false) Optional<Boolean> parking,
            @RequestParam(required = false) Optional<Boolean> vehicleManoeuvreArea,
            @RequestParam(required = false) Optional<Boolean> ownerTransport
            ) {
        List<Magazine> magazines = magazineService.searchMagazines(
                page.map(integer -> integer - 1).orElse(0),
                location,
                start,
                end,
                minArea,
                pricePerMeter,
                type,
                heating,
                light,
                whole,
                monitoring,
                antiTheftDoors,
                ventilation,
                smokeDetectors,
                selfService,
                floor,
                height,
                doorHeight,
                doorWidth,
                electricity,
                parking,
                vehicleManoeuvreArea,
                ownerTransport
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(magazines.stream().map(magazine -> {
                    MagazinePictureDto magazinePictureDto = imageServiceClient.getMagazinePictures(magazine.getId());
                    MagazinePreviewDto magazinePreviewDto = magazine.toMagazinePreviewDto();
                    magazinePreviewDto.setPhotos(magazinePictureDto.getPhotos());
                    return magazinePreviewDto;

                }).collect(Collectors.toList()));
    }

    @GetMapping("/availability/{id}")
    public ResponseEntity<Boolean> magazineAvailable(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NonNull LocalDate end,
            @RequestParam @Min(0) BigDecimal minArea
        ) {
        Optional<Magazine> magazine = magazineService.getMagazineDetails(id);
        return magazine.isEmpty() ?
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND).build() :
                ResponseEntity
                        .status(HttpStatus.OK)
                        .body(magazineService.checkIfMagazineAvailable(magazine.get(), start, end, minArea));

    }

    @GetMapping("/total-price/{id}")
    public ResponseEntity<BigDecimal> totalPrice(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NonNull LocalDate end,
            @RequestParam @Min(0) BigDecimal area
    ) {
        Optional<Magazine> magazine = magazineService.getMagazineDetails(id);
        return magazine.isEmpty() ?
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND).build() :
                ResponseEntity
                        .status(HttpStatus.OK)
                        .body(magazineService.getTotalPrice(magazine.get(), start, end,  area));
    }
}
