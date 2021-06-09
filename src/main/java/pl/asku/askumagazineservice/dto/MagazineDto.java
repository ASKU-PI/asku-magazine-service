package pl.asku.askumagazineservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pl.asku.askumagazineservice.model.Heating;
import pl.asku.askumagazineservice.model.Light;
import pl.asku.askumagazineservice.model.MagazineType;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
public class MagazineDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String owner;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate createdDate;

    private String location;

    private LocalDate startDate;

    private LocalDate endDate;

    private Float areaInMeters;

    private Float pricePerMeter;

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

    private Float height;

    private Float doorHeight;

    private Float doorWidth;

    private Boolean electricity;

    private Boolean parking;

    private Boolean vehicleManoeuvreArea;

    private Float minAreaToRent;

    private Boolean ownerTransport;

    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Float freeSpace;

    private List<String> imageIds;
}
