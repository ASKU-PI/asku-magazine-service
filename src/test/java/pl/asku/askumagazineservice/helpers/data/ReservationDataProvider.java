package pl.asku.askumagazineservice.helpers.data;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;
import pl.asku.askumagazineservice.exception.MagazineNotAvailableException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.service.ReservationService;

@Service
@AllArgsConstructor
public class ReservationDataProvider {

    ReservationService reservationService;

    public ReservationDto reservationDto(Magazine magazine) {
        return ReservationDto.builder()
                .areaInMeters(magazine.getMinAreaToRent())
                .startDate(magazine.getStartDate())
                .endDate(magazine.getEndDate())
                .magazineId(magazine.getId())
                .build();
    }

    public Reservation reservation(User user, Magazine magazine) throws MagazineNotAvailableException,
            MagazineNotFoundException {
        return reservation(user, reservationDto(magazine));
    }

    public Reservation reservation(User user, ReservationDto reservationDto) throws MagazineNotAvailableException,
            MagazineNotFoundException {
        return reservationService.addReservation(reservationDto, user.getId());
    }
}
