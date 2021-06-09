package pl.asku.askumagazineservice.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

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
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private Float areaInMeters;

    @NotNull
    private Float pricePerMeter;

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

    private Float height;

    private Float doorHeight;

    private Float doorWidth;

    private Boolean electricity;

    private Boolean parking;

    private Boolean vehicleManoeuvreArea;

    private Float minAreaToRent;

    private Boolean ownerTransport;

    private String description;

    @NotNull
    private Float freeSpace;

}
