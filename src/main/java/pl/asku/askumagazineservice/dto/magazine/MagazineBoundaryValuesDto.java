package pl.asku.askumagazineservice.dto.magazine;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MagazineBoundaryValuesDto {

  private BigDecimal minArea;
  private BigDecimal maxArea;
  private BigDecimal minTemperature;
  private BigDecimal maxTemperature;
  private BigDecimal minPricePerMeter;
  private BigDecimal maxPricePerMeter;
  private BigDecimal minDoorHeight;
  private BigDecimal minDoorWidth;
  private BigDecimal minHeight;

}
