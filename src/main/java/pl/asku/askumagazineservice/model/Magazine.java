package pl.asku.askumagazineservice.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.dto.MagazinePreviewDto;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "magazine")
@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Magazine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, updatable = false)
    private Long id;

    private LocalDate createdDate;

    @NotNull
    @NotBlank
    private String owner;

    @NotNull
    @NotBlank
    private String location;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @NotNull
    private BigDecimal areaInMeters;

    @NotNull
    private BigDecimal pricePerMeter;

    @Enumerated(EnumType.STRING)
    private MagazineType type;

    @Enumerated(EnumType.STRING)
    private Heating heating;

    @Enumerated(EnumType.STRING)
    private Light light;

    private Boolean whole;

    private Boolean monitoring;

    private Boolean antiTheftDoors;

    private Boolean ventilation;

    private Boolean smokeDetectors;

    private Boolean selfService;

    private Integer floor;

    private BigDecimal height;

    private BigDecimal doorHeight;

    private BigDecimal doorWidth;

    private Boolean electricity;

    private Boolean parking;

    private Boolean vehicleManoeuvreArea;

    private BigDecimal minAreaToRent;

    private Boolean ownerTransport;

    private String description;

    public MagazineDto toMagazineDto() {
        return new MagazineDto(
                id,
                owner,
                createdDate,
                null,
                location,
                startDate,
                endDate,
                areaInMeters,
                pricePerMeter,
                type,
                heating,
                light,
                whole,
                monitoring,
                antiTheftDoors,
                ventilation,
                smokeDetectors,
                selfService,
                floor,
                height,
                doorHeight,
                doorWidth,
                electricity,
                parking,
                vehicleManoeuvreArea,
                minAreaToRent,
                ownerTransport,
                description
        );
    }

    public MagazinePreviewDto toMagazinePreviewDto() {
        return new MagazinePreviewDto(
                id,
                owner,
                createdDate,
                null,
                location,
                startDate,
                endDate,
                areaInMeters,
                pricePerMeter,
                type
        );
    }
}
