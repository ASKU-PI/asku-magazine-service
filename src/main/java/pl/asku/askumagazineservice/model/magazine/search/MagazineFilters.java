package pl.asku.askumagazineservice.model.magazine.search;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.asku.askumagazineservice.model.magazine.Heating;
import pl.asku.askumagazineservice.model.magazine.Light;
import pl.asku.askumagazineservice.model.magazine.MagazineType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MagazineFilters {
  private LocationFilter locationFilter;
  private LocalDate startDateGreaterOrEqual;
  private LocalDate endDateLessOrEqual;
  private BigDecimal minFreeArea;
  private BigDecimal maxFreeArea;
  private BigDecimal maxPricePerMeter;
  private BigDecimal minPricePerMeter;
  private String ownerIdentifier;
  private MagazineType type;
  private Heating heating;
  private Light light;
  private Boolean isWhole;
  private Boolean hasMonitoring;
  private Boolean hasAntiTheftDoors;
  private Boolean hasVentilation;
  private Boolean hasSmokeDetectors;
  private Boolean isSelfService;
  private Integer minFloor;
  private Integer maxFloor;
  private BigDecimal minDoorHeight;
  private BigDecimal minDoorWidth;
  private BigDecimal minHeight;
  private Boolean hasElectricity;
  private Boolean hasParking;
  private Boolean hasElevator;
  private Boolean hasVehicleManoeuvreArea;
  private Boolean canOwnerTransport;
  private Boolean availableOnly;
  private String currentlyReservedBy;
  private String historicallyReservedBy;
  private BigDecimal minTemperature;
  private BigDecimal maxTemperature;
  private Boolean withDeleted;
}
