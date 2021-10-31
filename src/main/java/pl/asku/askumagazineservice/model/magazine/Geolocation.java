package pl.asku.askumagazineservice.model.magazine;

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
public class Geolocation {

  private BigDecimal longitude;
  private BigDecimal latitude;
}
