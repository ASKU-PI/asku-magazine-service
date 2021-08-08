package pl.asku.askumagazineservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.asku.askumagazineservice.dto.ReservationDto;
import pl.asku.askumagazineservice.model.Reservation;
import pl.asku.askumagazineservice.service.MagazineService;

import java.util.Optional;

@RestController
@RequestMapping("/api/reservation")
@AllArgsConstructor
public class ReservationController {

    private final MagazineService magazineService;

    @PostMapping("/add")
    public ResponseEntity addReservation(
            @RequestBody ReservationDto reservationDto,
            Authentication authentication){
        String username = authentication.getName();
        if(username.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You must pass Username in the header!");
        }
        Reservation reservation = magazineService.addReservationAndUpdateMagazineFreeSpace(reservationDto, username);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Reservation created: " + reservation.getId());
    }
}
