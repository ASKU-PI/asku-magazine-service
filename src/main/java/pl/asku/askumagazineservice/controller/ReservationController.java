package pl.asku.askumagazineservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.asku.askumagazineservice.dto.ReservationDto;
import pl.asku.askumagazineservice.model.Reservation;
import pl.asku.askumagazineservice.security.policy.ReservationPolicy;
import pl.asku.askumagazineservice.service.MagazineService;

import java.util.Optional;

@RestController
@RequestMapping("/api/reservation")
@AllArgsConstructor
public class ReservationController {

    private final MagazineService magazineService;
    private final ReservationPolicy reservationPolicy;

    @PostMapping("/add")
    public ResponseEntity<ReservationDto> addReservation(
            @RequestBody ReservationDto reservationDto,
            Authentication authentication){
        if(!reservationPolicy.addReservation(authentication))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reservationDto);

        String username = authentication.getName();

        Optional<Reservation> reservation = magazineService.addReservation(reservationDto, username);

        if(reservation.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        reservationDto.setId(reservation.get().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationDto);
    }
}
