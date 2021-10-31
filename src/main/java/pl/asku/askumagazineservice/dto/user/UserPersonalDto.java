package pl.asku.askumagazineservice.dto.user;

import lombok.*;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPersonalDto {

    private UserDto user;

    private List<MagazineDto> activeSpaces;

    private List<ReservationDto> activeReservations;
}
