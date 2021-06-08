package pl.asku.askumagazineservice.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "magazine")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Magazine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date createdDate;

    private String owner;

    private String location;

    private Date startDate;

    private Date endDate;

    private Float areaInMeters;

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

}
