package pl.asku.askumagazineservice.dto.reservation;

import lombok.*;
import pl.asku.askumagazineservice.model.reservation.AvailabilityState;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyStateDto {
    private Long magazineId;
    private LocalDate day;
    private AvailabilityState availabilityState;
}
