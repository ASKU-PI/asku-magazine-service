package pl.asku.askumagazineservice.controller;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.asku.askumagazineservice.client.GeocodingClient;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.magazine.service.MagazineService;
import pl.asku.askumagazineservice.model.Heating;
import pl.asku.askumagazineservice.model.Light;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.model.MagazineType;
import pl.asku.askumagazineservice.model.search.LocationFilter;
import pl.asku.askumagazineservice.model.search.MagazineFilters;
import pl.asku.askumagazineservice.model.search.SortOptions;
import pl.asku.askumagazineservice.security.policy.MagazinePolicy;
import pl.asku.askumagazineservice.util.modelconverter.MagazineConverter;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping("/api")
@AllArgsConstructor
public class MagazineController {

    private final int MAX_PHOTOS_PER_UPLOAD = 20;

    private final MagazineService magazineService;
    private final MagazinePolicy magazinePolicy;
    private final GeocodingClient geocodingClient;
    private final MagazineConverter magazineConverter;

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<Object> addMagazine(
            @ModelAttribute @Valid MagazineDto magazineDto,
            @RequestPart(value = "files", required = false) MultipartFile[] photos,
            Authentication authentication) {
        if (photos != null && photos.length > MAX_PHOTOS_PER_UPLOAD)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Max photos number is " + MAX_PHOTOS_PER_UPLOAD);

        if (!magazinePolicy.addMagazine(authentication))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You're not authorized to add space to rent");

        String identifier = authentication.getName();

        try {
            Magazine magazine = magazineService.addMagazine(magazineDto, identifier, photos);
            magazineDto = magazineConverter.toDto(magazine);
        } catch (ValidationException | LocationNotFoundException | LocationIqRequestFailedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(magazineDto);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<Object> getMagazineDetails(@Valid @PathVariable @NotNull Long id) {
        Optional<Magazine> magazine = magazineService.getMagazineDetails(id);
        if (magazine.isPresent()) {
            MagazineDto magazineDto = magazineConverter.toDto(magazine.get());
            return ResponseEntity.status(HttpStatus.OK).body(magazineDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Space not found");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchMagazines(
            @RequestParam(required = false) Optional<Integer> page,
            @RequestParam(required = false) Optional<SortOptions> sortBy,
            @RequestParam(required = false) Optional<BigDecimal> minLongitude,
            @RequestParam(required = false) Optional<BigDecimal> maxLongitude,
            @RequestParam(required = false) Optional<BigDecimal> minLatitude,
            @RequestParam(required = false) Optional<BigDecimal> maxLatitude,
            @RequestParam(required = false) Optional<String> location,
            @RequestParam(required = false) Optional<BigDecimal> radiusInKilometers,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> end,
            @RequestParam(required = false) Optional<BigDecimal> minArea,
            @RequestParam(required = false) Optional<BigDecimal> maxArea,
            @RequestParam(required = false) Optional<BigDecimal> minTemperature,
            @RequestParam(required = false) Optional<BigDecimal> maxTemperature,
            @RequestParam(required = false) Optional<BigDecimal> minPricePerMeter,
            @RequestParam(required = false) Optional<BigDecimal> maxPricePerMeter,
            @RequestParam(required = false) Optional<String> ownerIdentifier,
            @RequestParam(required = false) Optional<Boolean> availableOnly,
            @RequestParam(required = false) Optional<String> currentlyReservedBy,
            @RequestParam(required = false) Optional<String> historicallyReservedBy,
            @RequestParam(required = false) Optional<MagazineType> type,
            @RequestParam(required = false) Optional<Heating> heating,
            @RequestParam(required = false) Optional<Light> light,
            @RequestParam(required = false) Optional<Boolean> wholeLocation,
            @RequestParam(required = false) Optional<Boolean> elevator,
            @RequestParam(required = false) Optional<Boolean> monitoring,
            @RequestParam(required = false) Optional<Boolean> antiTheftDoors,
            @RequestParam(required = false) Optional<Boolean> ventilation,
            @RequestParam(required = false) Optional<Boolean> smokeDetectors,
            @RequestParam(required = false) Optional<Boolean> selfService,
            @RequestParam(required = false) Optional<Integer> minFloor,
            @RequestParam(required = false) Optional<Integer> maxFloor,
            @RequestParam(required = false) Optional<BigDecimal> doorHeight,
            @RequestParam(required = false) Optional<BigDecimal> doorWidth,
            @RequestParam(required = false) Optional<BigDecimal> height,
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
            locationFilter = new LocationFilter(minLongitude.orElse(null), maxLongitude.orElse(null),
                    minLatitude.orElse(null), maxLatitude.orElse(null));
        }

        MagazineFilters filters = new MagazineFilters(
                locationFilter,
                start.orElse(null),
                end.orElse(null),
                minArea.orElse(null),
                maxArea.orElse(null),
                maxPricePerMeter.orElse(null),
                minPricePerMeter.orElse(null),
                ownerIdentifier.orElse(null),
                type.orElse(null),
                heating.orElse(null),
                light.orElse(null),
                wholeLocation.orElse(null),
                monitoring.orElse(null),
                antiTheftDoors.orElse(null),
                ventilation.orElse(null),
                smokeDetectors.orElse(null),
                selfService.orElse(null),
                minFloor.orElse(null),
                maxFloor.orElse(null),
                doorHeight.orElse(null),
                doorWidth.orElse(null),
                height.orElse(null),
                electricity.orElse(null),
                parking.orElse(null),
                elevator.orElse(null),
                vehicleManoeuvreArea.orElse(null),
                ownerTransport.orElse(null),
                availableOnly.orElse(null),
                currentlyReservedBy.orElse(null),
                historicallyReservedBy.orElse(null),
                minTemperature.orElse(null),
                maxTemperature.orElse(null)
        );

        try {
            List<Magazine> magazines = magazineService.searchMagazines(
                    page.isPresent() && page.get() > 0 ? page.get() : 1,
                    filters,
                    sortBy.orElse(null)
            );
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(magazines.stream().map(magazineConverter::toPreviewDto).collect(Collectors.toList()));
        } catch (ValidationException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/total-price/{id}")
    public ResponseEntity<Object> totalPrice(
            @PathVariable @NotNull Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NonNull LocalDate end,
            @RequestParam @Min(0) BigDecimal area
    ) {
        Optional<Magazine> magazine = magazineService.getMagazineDetails(id);

        if (magazine.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Space not found");

        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(magazineService.getTotalPrice(magazine.get(), start, end, area));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/max-area")
    public ResponseEntity<Object> maxArea() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(magazineService.maxArea());
        } catch (MagazineNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No magazines found");
        }
    }
}
