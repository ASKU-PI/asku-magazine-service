package pl.asku.askumagazineservice.model.magazine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import pl.asku.askumagazineservice.model.User;

@Entity
@Table(name = "magazine")
@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Magazine {

  @NotNull
  @Embedded
  Geolocation location;
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
  private boolean deleted = Boolean.FALSE;
  private boolean available = Boolean.TRUE;
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", referencedColumnName = "id")
  private User owner;
  @NotNull
  @NotBlank
  @Size(min = 3, max = 100)
  private String title;
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

  private Boolean light;

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

  private Boolean elevator;

  private Boolean vehicleManoeuvreArea;

  @Min(1)
  private BigDecimal minAreaToRent;

  private Boolean ownerTransport;

  private String description;

  private BigDecimal minTemperature;

  private BigDecimal maxTemperature;
}
