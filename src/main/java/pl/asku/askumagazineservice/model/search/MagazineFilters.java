package pl.asku.askumagazineservice.model.search;

import lombok.*;
import pl.asku.askumagazineservice.model.Heating;
import pl.asku.askumagazineservice.model.Light;
import pl.asku.askumagazineservice.model.MagazineType;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    private Boolean hasElectricity;
    private Boolean hasParking;
    private Boolean hasVehicleManoeuvreArea;
    private Boolean canOwnerTransport;
}
