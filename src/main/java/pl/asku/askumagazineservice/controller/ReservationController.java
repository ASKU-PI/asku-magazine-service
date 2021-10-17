package pl.asku.askumagazineservice.controller;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.asku.askumagazineservice.dto.ReservationDto;
import pl.asku.askumagazineservice.exception.MagazineNotAvailableException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.magazine.service.MagazineService;
import pl.asku.askumagazineservice.magazine.service.ReservationService;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.model.Reservation;
import pl.asku.askumagazineservice.security.policy.ReservationPolicy;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api/reservation")
@AllArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final MagazineService magazineService;
    private final ReservationPolicy reservationPolicy;

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

    @GetMapping("/availability/{id}")
    public ResponseEntity<Object> magazineAvailable(
            @PathVariable @NotNull Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NonNull LocalDate end,
            @RequestParam @Min(0) BigDecimal minArea
    ) {
        Optional<Magazine> magazine = magazineService.getMagazineDetails(id);

        if (magazine.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Space not found");

        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reservationService.checkIfMagazineAvailable(magazine.get(), start, end, minArea));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
