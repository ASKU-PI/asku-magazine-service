package pl.asku.askumagazineservice.dto.magazine;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import pl.asku.askumagazineservice.model.magazine.Heating;
import pl.asku.askumagazineservice.model.magazine.Light;
import pl.asku.askumagazineservice.model.magazine.MagazineType;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MagazineUpdateDto {
  Boolean available;

  @Size(min = 3, max = 100)
  private String title;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate endDate;

  @Min(0)
  private BigDecimal areaInMeters;

  @Min(0)
  private BigDecimal pricePerMeter;

  private MagazineType type;

  private Heating heating;

  private Light light;

  private Boolean whole;

  private Boolean monitoring;

  private Boolean antiTheftDoors;

  private Boolean ventilation;

  private Boolean smokeDetectors;

  private Boolean selfService;

  private Integer floor;

  @Min(0)
  private BigDecimal height;

  @Min(0)
  private BigDecimal doorHeight;

  @Min(0)
  private BigDecimal doorWidth;

  private Boolean electricity;

  private Boolean parking;

  private Boolean elevator;

  private Boolean vehicleManoeuvreArea;

  @Min(1)
  private BigDecimal minAreaToRent;

  private Boolean ownerTransport;

  @Size(min = 3, max = 500)
  private String description;

  private BigDecimal minTemperature;

  private BigDecimal maxTemperature;
}
