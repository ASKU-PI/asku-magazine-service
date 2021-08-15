package pl.asku.askumagazineservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pl.asku.askumagazineservice.model.Heating;
import pl.asku.askumagazineservice.model.Light;
import pl.asku.askumagazineservice.model.MagazineType;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
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

    @NonNull
    @Size(min = 2, max = 50)
    private String location;

    @NonNull
    private LocalDate startDate;

    @NonNull
    private LocalDate endDate;

    @NonNull
    @Min(0)
    private Float areaInMeters;

    @NonNull
    @Min(0)
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

    @Min(0)
    private Float height;

    @Min(0)
    private Float doorHeight;

    @Min(0)
    private Float doorWidth;

    private Boolean electricity;

    private Boolean parking;

    private Boolean vehicleManoeuvreArea;

    @NonNull
    @Min(1)
    private Float minAreaToRent;

    private Boolean ownerTransport;

    @Size(min = 3, max = 500)
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Float freeSpace;

    private List<String> imageIds;
}
