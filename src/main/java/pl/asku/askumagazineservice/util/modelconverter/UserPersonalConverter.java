package pl.asku.askumagazineservice.util.modelconverter;

import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.user.UserPersonalDto;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;

@Service
@AllArgsConstructor
public class UserPersonalConverter {

  private final MagazineService magazineService;
  @Lazy
  private final MagazineConverter magazineConverter;

  private final ReservationService reservationService;
  private final ReservationConverter reservationConverter;

  private final UserConverter userConverter;

  public UserPersonalDto toPersonalDto(User user) {
    return UserPersonalDto.builder()
        .user(userConverter.toDto(user))
        .activeSpaces(
            magazineService.getActiveByOwner(user.getId())
                .stream()
                .map(magazineConverter::toDto)
                .collect(Collectors.toList())
        )
        .activeReservations(
            reservationService.getUserActive(user.getId())
                .stream()
                .map(reservationConverter::toDto)
                .collect(Collectors.toList())
        )
        .build();
  }
}
