package pl.asku.askumagazineservice.dto.user;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPersonalDto {

  private UserDto user;

  private List<MagazineDto> activeSpaces;

  private List<MagazineDto> deactivatedSpaces;

  private List<ReservationDto> activeReservations;

  private List<ReservationDto> pastReservations;
}
