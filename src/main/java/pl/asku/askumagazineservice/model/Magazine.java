package pl.asku.askumagazineservice.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, updatable = false)
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @NotNull
    @NotBlank
    private String owner;

    @NotNull
    @NotBlank
    private String country;

    @NotNull
    @NotBlank
    private String city;

    @NotNull
    @NotBlank
    private String street;

    @NotNull
    @NotBlank
    private String building;

    @NotNull
    private BigDecimal longitude;

    @NotNull
    private BigDecimal latitude;

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

    @Min(1)
    private BigDecimal minAreaToRent;

    private Boolean ownerTransport;

    private String description;
}
