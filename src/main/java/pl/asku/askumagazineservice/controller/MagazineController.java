package pl.asku.askumagazineservice.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.asku.askumagazineservice.client.GeocodingClient;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Heating;
import pl.asku.askumagazineservice.model.magazine.Light;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.magazine.MagazineType;
import pl.asku.askumagazineservice.model.magazine.search.LocationFilter;
import pl.asku.askumagazineservice.model.magazine.search.MagazineFilters;
import pl.asku.askumagazineservice.model.magazine.search.MagazineSearchResult;
import pl.asku.askumagazineservice.model.magazine.search.SortOptions;
import pl.asku.askumagazineservice.security.policy.MagazinePolicy;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.UserService;
import pl.asku.askumagazineservice.util.modelconverter.MagazineConverter;
import pl.asku.askumagazineservice.util.modelconverter.SearchResultConverter;

@RestController
@Validated
@RequestMapping("/api")
@AllArgsConstructor
public class MagazineController {

  private final MagazineService magazineService;
  private final MagazinePolicy magazinePolicy;
  private final GeocodingClient geocodingClient;
  @Lazy
  private final MagazineConverter magazineConverter;
  private final SearchResultConverter searchResultConverter;

  private final UserService userService;

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
    return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(),
        HttpStatus.BAD_REQUEST);
  }

  @PostMapping(value = "/add", consumes = "multipart/form-data")
  public ResponseEntity<Object> addMagazine(
      @ModelAttribute @Valid MagazineDto magazineDto,
      @RequestPart(value = "files", required = false) MultipartFile[] photos,
      Authentication authentication) {

    if (!magazinePolicy.addMagazine(authentication)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("You're not authorized to add a space to rent");
    }

    try {
      User user = userService.getUser(authentication.getName());
      Magazine magazine = magazineService.addMagazine(magazineDto, user, photos);
      magazineDto = magazineConverter.toDto(magazine);
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
    } catch (ValidationException | LocationNotFoundException | LocationIqRequestFailedException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(magazineDto);
  }

  @PatchMapping(value = "/magazine", consumes = "multipart/form-data")
  public ResponseEntity<Object> updateMagazine(
      @RequestParam Long magazineId,
      @ModelAttribute @Valid MagazineDto magazineDto,
      @RequestParam(required = false) List<String> toDeletePhotosIds,
      @RequestPart(value = "files", required = false) MultipartFile[] toAddPhotos,
      Authentication authentication) {
    try {
      Magazine magazine = magazineService.getMagazine(magazineId);

      if (!magazinePolicy.updateMagazine(authentication, magazine)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("You are not authorized to update this space");
      }

      return ResponseEntity.status(HttpStatus.OK).body(
          magazineConverter.toDto(magazineService.updateMagazine(
              magazine, magazineDto, toDeletePhotosIds, toAddPhotos)));
    } catch (MagazineNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @DeleteMapping("/magazine")
  public ResponseEntity<Object> deleteMagazine(
      @RequestParam Long id,
      Authentication authentication
  ) {
    try {
      Magazine magazine = magazineService.getMagazine(id);
      if (!magazinePolicy.deleteMagazine(authentication, magazine)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("You are not authorized to delete this space");
      }
      return ResponseEntity.status(HttpStatus.OK).body(magazineService.deleteMagazine(magazine));
    } catch (MagazineNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/details/{id}")
  public ResponseEntity<Object> getMagazine(@PathVariable Long id) {
    try {
      Magazine magazine = magazineService.getMagazine(id);
      return ResponseEntity.status(HttpStatus.OK).body(magazineConverter.toDto(magazine));
    } catch (MagazineNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/search")
  public ResponseEntity<Object> getMagazinesWithPagination(
      @RequestParam(required = false) Optional<Integer> page,
      @RequestParam(required = false) Optional<SortOptions> sortBy,
      @RequestParam(required = false) Optional<BigDecimal> minLongitude,
      @RequestParam(required = false) Optional<BigDecimal> maxLongitude,
      @RequestParam(required = false) Optional<BigDecimal> minLatitude,
      @RequestParam(required = false) Optional<BigDecimal> maxLatitude,
      @RequestParam(required = false) Optional<String> location,
      @RequestParam(required = false) Optional<BigDecimal> radiusInKilometers,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Optional<LocalDate> start,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Optional<LocalDate> end,
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
        if (radiusInKilometers.isEmpty()) {
          locationFilter = new LocationFilter(location.get(), geocodingClient);
        } else {
          locationFilter =
              new LocationFilter(location.get(), radiusInKilometers.get(), geocodingClient);
        }
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
        maxTemperature.orElse(null),
        false
    );

    try {
      MagazineSearchResult result = magazineService.searchMagazines(
          page.isPresent() && page.get() > 0 ? page.get() : 1,
          filters,
          sortBy.orElse(null)
      );
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(searchResultConverter.toDto(result));
    } catch (ValidationException | UserNotFoundException e) {
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
