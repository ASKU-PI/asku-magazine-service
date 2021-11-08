package pl.asku.askumagazineservice.dto.reservation;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyAvailabilityDto {
  private Long magazineId;
  private LocalDate day;
  private Boolean isAvailable;
}