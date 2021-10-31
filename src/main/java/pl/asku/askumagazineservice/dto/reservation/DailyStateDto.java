package pl.asku.askumagazineservice.dto.reservation;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.asku.askumagazineservice.model.reservation.AvailabilityState;

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
