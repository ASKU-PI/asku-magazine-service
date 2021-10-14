package pl.asku.askumagazineservice.controller;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.asku.askumagazineservice.client.GeocodingClient;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.dto.MagazinePreviewDto;
import pl.asku.askumagazineservice.dto.imageservice.MagazinePictureDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.model.Heating;
import pl.asku.askumagazineservice.model.Light;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.model.MagazineType;
import pl.asku.askumagazineservice.model.search.LocationFilter;
import pl.asku.askumagazineservice.model.search.MagazineFilters;
import pl.asku.askumagazineservice.security.policy.MagazinePolicy;
import pl.asku.askumagazineservice.service.MagazineService;

import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final GeocodingClient geocodingClient;

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<MagazineDto> addMagazine(
            @ModelAttribute Magazine magazine,
            @RequestPart(value = "files", required = false) MultipartFile[] photos,
            Authentication authentication) {
        if (!magazinePolicy.addMagazine(authentication))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(magazine.toMagazineDto());

        String identifier = authentication.getName();

        MagazinePictureDto magazinePictureDto = null;

        try {
            magazine = magazineService.addMagazine(magazine.toMagazineDto(), identifier);
            if (photos != null)
                magazinePictureDto = imageServiceClient.uploadMagazinePictures(magazine.getId(), photos);
        } catch (ValidationException | IOException | LocationNotFoundException | LocationIqRequestFailedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        MagazineDto magazineDto = magazine.toMagazineDto();
        magazineDto.setId(magazine.getId());
        if (photos != null) magazineDto.setPhotos(magazinePictureDto.getPhotos());

        return ResponseEntity.status(HttpStatus.CREATED).body(magazineDto);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<MagazineDto> getMagazineDetails(@PathVariable Long id) {
        Optional<Magazine> magazine = magazineService.getMagazineDetails(id);
        if (magazine.isPresent()) {
            MagazineDto magazineDto = magazine.get().toMagazineDto();
            try {
                MagazinePictureDto magazinePictureDto = imageServiceClient.getMagazinePictures(magazine.get().getId());
                magazineDto.setPhotos(magazinePictureDto.getPhotos());
            } catch(Exception e) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.OK).body(magazineDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<MagazinePreviewDto>> searchMagazines(
            @RequestParam(required = false) Optional<Integer> page,
            @RequestParam(required = false) Optional<BigDecimal> minLongitude,
            @RequestParam(required = false) Optional<BigDecimal> maxLongitude,
            @RequestParam(required = false) Optional<BigDecimal> minLatitude,
            @RequestParam(required = false) Optional<BigDecimal> maxLatitude,
            @RequestParam(required = false) Optional<String> location,
            @RequestParam(required = false) Optional<BigDecimal> radiusInKilometers,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> end,
            @RequestParam(required = false) Optional<BigDecimal> minArea,
            @RequestParam(required = false) Optional<BigDecimal> pricePerMeter,
            @RequestParam(required = false) Optional<String> ownerIdentifier,
            @RequestParam(required = false) Optional<Boolean> availableOnly, //TODO
            @RequestParam(required = false) Optional<String> currentlyReservedBy, //TODO
            @RequestParam(required = false) Optional<String> historicallyReservedBy, //TODO
            @RequestParam(required = false) Optional<MagazineType> type,
            @RequestParam(required = false) Optional<Heating> heating,
            @RequestParam(required = false) Optional<Light> light,
            @RequestParam(required = false) Optional<Boolean> whole,
            @RequestParam(required = false) Optional<Boolean> monitoring,
            @RequestParam(required = false) Optional<Boolean> antiTheftDoors,
            @RequestParam(required = false) Optional<Boolean> ventilation,
            @RequestParam(required = false) Optional<Boolean> smokeDetectors,
            @RequestParam(required = false) Optional<Boolean> selfService,
            @RequestParam(required = false) Optional<Integer> minFloor,
            @RequestParam(required = false) Optional<Integer> maxFloor,
            @RequestParam(required = false) Optional<BigDecimal> doorHeight,
            @RequestParam(required = false) Optional<BigDecimal> doorWidth,
            @RequestParam(required = false) Optional<Boolean> electricity,
            @RequestParam(required = false) Optional<Boolean> parking,
            @RequestParam(required = false) Optional<Boolean> vehicleManoeuvreArea,
            @RequestParam(required = false) Optional<Boolean> ownerTransport
    ) {
        LocationFilter locationFilter;

        if (location.isPresent()) {
            try {
                if (radiusInKilometers.isEmpty()) locationFilter = new LocationFilter(location.get(), geocodingClient);
                else locationFilter = new LocationFilter(location.get(), radiusInKilometers.get(), geocodingClient);
            } catch (LocationNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ArrayList<>());
            } catch (LocationIqRequestFailedException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
        } else {
            locationFilter = new LocationFilter(minLongitude.orElse(null), maxLongitude.orElse(null), minLatitude.orElse(null), maxLatitude.orElse(null));
        }

        MagazineFilters filters = new MagazineFilters(
                locationFilter,
                start.orElse(null),
                end.orElse(null),
                minArea.orElse(null),
                pricePerMeter.orElse(null),
                ownerIdentifier.orElse(null),
                type.orElse(null),
                heating.orElse(null),
                light.orElse(null),
                whole.orElse(null),
                monitoring.orElse(null),
                antiTheftDoors.orElse(null),
                ventilation.orElse(null),
                smokeDetectors.orElse(null),
                selfService.orElse(null),
                minFloor.orElse(null),
                maxFloor.orElse(null),
                doorHeight.orElse(null),
                doorWidth.orElse(null),
                electricity.orElse(null),
                parking.orElse(null),
                vehicleManoeuvreArea.orElse(null),
                ownerTransport.orElse(null)
        );

        List<Magazine> magazines = magazineService.searchMagazines(
                page.isPresent() && page.get() > 0 ? page.get() : 1,
                filters
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(magazines.stream().map(magazine -> {
                    MagazinePreviewDto magazinePreviewDto = magazine.toMagazinePreviewDto();
                    try {
                        MagazinePictureDto magazinePictureDto = imageServiceClient.getMagazinePictures(magazine.getId());
                        magazinePreviewDto.setPhotos(magazinePictureDto.getPhotos());
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
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
                        .body(magazineService.getTotalPrice(magazine.get(), start, end, area));
    }
}
