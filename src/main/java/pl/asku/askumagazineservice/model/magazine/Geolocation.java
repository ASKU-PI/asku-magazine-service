package pl.asku.askumagazineservice.model.magazine;

import java.math.BigDecimal;
import javax.persistence.Embeddable;
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
@Embeddable
public class Geolocation {

  private BigDecimal longitude;
  private BigDecimal latitude;
}
