package pl.asku.askumagazineservice.controller;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.asku.askumagazineservice.dto.reservation.AvailableSpaceDto;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;
import pl.asku.askumagazineservice.exception.MagazineNotAvailableException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.model.Reservation;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.security.policy.ReservationPolicy;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;
import pl.asku.askumagazineservice.util.modelconverter.ReservationConverter;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping("/api/reservation")
@AllArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final MagazineService magazineService;
    private final ReservationPolicy reservationPolicy;
    private final ReservationConverter reservationConverter;

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addReservation(
            @RequestBody @Valid ReservationDto reservationDto,
            Authentication authentication) {
        if (!reservationPolicy.addReservation(authentication))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reservationDto);

        String username = authentication.getName();

        try {
            Reservation reservation = reservationService.addReservation(reservationDto, username);
            reservationDto.setId(reservation.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(reservationDto);
        } catch (MagazineNotAvailableException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (MagazineNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/daily-reservations")
    public ResponseEntity<Object> getDailyReservations(
            @RequestBody Long spaceId,
            @RequestBody LocalDate day,
            Authentication authentication
    ) {
        try {
            if (!reservationPolicy.getReservations(authentication, spaceId))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("You're not authorized to get reservations of this space");
        } catch (MagazineNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        reservationService.getDailyReservations(spaceId, day)
                                .stream()
                                .map(reservationConverter::toDto)
                                .collect(Collectors.toList()
                                ));
    }


    @GetMapping("/availability/{id}")
    public ResponseEntity<Object> magazineAvailable(
            @PathVariable @NotNull Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NonNull LocalDate end,
            @RequestParam @Min(0) BigDecimal minArea
    ) {
        try {
            Magazine magazine = magazineService.getMagazineDetails(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reservationService.checkIfMagazineAvailable(magazine, start, end, minArea));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (MagazineNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    @GetMapping("/available-area/{id}")
    public ResponseEntity<Object> availableArea(
            @PathVariable @NotNull Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NonNull LocalDate end
    ) {
        try {
            Magazine magazine = magazineService.getMagazineDetails(id);
            BigDecimal availableArea = reservationService.getAvailableArea(magazine, start, end);
            return ResponseEntity.status(HttpStatus.OK).body(new AvailableSpaceDto(magazine.getId(), availableArea));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (MagazineNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
