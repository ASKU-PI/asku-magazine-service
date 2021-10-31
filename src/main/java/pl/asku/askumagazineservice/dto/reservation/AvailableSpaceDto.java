package pl.asku.askumagazineservice.dto.reservation;

import java.math.BigDecimal;
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
public class AvailableSpaceDto {
  private Long magazineId;
  private BigDecimal availableArea;
}
