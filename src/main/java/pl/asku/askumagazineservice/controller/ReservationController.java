package pl.asku.askumagazineservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.asku.askumagazineservice.dto.ReservationDto;
import pl.asku.askumagazineservice.magazine.service.MagazineService;
import pl.asku.askumagazineservice.model.Reservation;
import pl.asku.askumagazineservice.security.policy.ReservationPolicy;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api/reservation")
@AllArgsConstructor
public class ReservationController {

    private final MagazineService magazineService;
    private final ReservationPolicy reservationPolicy;

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/add")
    public ResponseEntity<ReservationDto> addReservation(
            @RequestBody @Valid ReservationDto reservationDto,
            Authentication authentication) {
        if (!reservationPolicy.addReservation(authentication))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reservationDto);

        String username = authentication.getName();

        Optional<Reservation> reservation = magazineService.addReservation(reservationDto, username);

        if (reservation.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        reservationDto.setId(reservation.get().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationDto);
    }
}
