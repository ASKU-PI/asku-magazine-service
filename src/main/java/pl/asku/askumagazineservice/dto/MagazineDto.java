package pl.asku.askumagazineservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pl.asku.askumagazineservice.dto.imageservice.PictureData;
import pl.asku.askumagazineservice.model.Heating;
import pl.asku.askumagazineservice.model.Light;
import pl.asku.askumagazineservice.model.MagazineType;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


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
    private LocalDate startDate;

    @NotNull
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

    private Boolean vehicleManoeuvreArea;

    @NotNull
    @Min(1)
    private BigDecimal minAreaToRent;

    private Boolean ownerTransport;

    @Size(min = 3, max = 500)
    private String description;


    public List<String> getViolationMessages() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        List<String> violations = validator
                .validate(this)
                .stream().map(Object::toString)
                .collect(Collectors.toList());

        if (startDate.compareTo(endDate) >= 0) violations.add("Start date is not earlier than end date");
        if (minAreaToRent.compareTo(areaInMeters) > 0)
            violations.add("Min area to rent must be lower or equal total area");

        return violations;
    }
}
