package pl.asku.askumagazineservice.util.modelconverter;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;
import pl.asku.askumagazineservice.model.reservation.Reservation;

@Service
@AllArgsConstructor
public class ReservationConverter {

  @Lazy
  public MagazineConverter magazineConverter;
  public UserConverter userConverter;

  public ReservationDto toDto(Reservation reservation) {
    return ReservationDto.builder()
        .id(reservation.getId())
        .areaInMeters(reservation.getAreaInMeters())
        .startDate(reservation.getStartDate())
        .endDate(reservation.getEndDate())
        .createdDate(reservation.getCreatedDate())
        .magazineId(reservation.getMagazine().getId())
        .magazine(magazineConverter.toDto(reservation.getMagazine()))
        .user(userConverter.toDto(reservation.getUser()))
        .build();
  }
}
