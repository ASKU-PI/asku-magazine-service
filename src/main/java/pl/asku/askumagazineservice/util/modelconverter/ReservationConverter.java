package pl.asku.askumagazineservice.util.modelconverter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;
import pl.asku.askumagazineservice.model.reservation.Reservation;

@Service
@AllArgsConstructor
public class ReservationConverter {

    public ReservationDto toDto(Reservation reservation) {
        return ReservationDto.builder()
                .areaInMeters(reservation.getAreaInMeters())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .createdDate(reservation.getCreatedDate())
                .magazineId(reservation.getMagazine().getId())
                .user(reservation.getUserId())
                .build();
    }
}
