package pl.asku.askumagazineservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import pl.asku.askumagazineservice.dto.imageservice.PictureData;
import pl.asku.askumagazineservice.model.Heating;
import pl.asku.askumagazineservice.model.Light;
import pl.asku.askumagazineservice.model.MagazineType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MagazineDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String owner;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createdDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<PictureData> photos;

    @NotNull
    @Size(min = 2, max = 50)
    private String country;

    @NotNull
    @Size(min = 2, max = 50)
    private String city;

    @NotNull
    @Size(min = 2, max = 50)
    private String street;

    @NotNull
    @Size(min = 2, max = 50)
    private String building;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal longitude;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal latitude;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @NotNull
    @Min(0)
    private BigDecimal areaInMeters;

    @NotNull
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

    @NotNull
    @Min(1)
    private BigDecimal minAreaToRent;

    private Boolean ownerTransport;

    @Size(min = 3, max = 500)
    private String description;

    private BigDecimal minTemperature;

    private BigDecimal maxTemperature;
}
