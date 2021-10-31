package pl.asku.askumagazineservice.dto.magazine;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.asku.askumagazineservice.dto.client.imageservice.PictureData;
import pl.asku.askumagazineservice.model.magazine.MagazineType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MagazinePreviewDto {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String owner;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date createdDate;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private List<PictureData> photos;

  @NotNull
  @Size(min = 3, max = 100)
  private String title;

  private String country;

  private String city;

  private String street;

  private String building;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private BigDecimal longitude;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private BigDecimal latitude;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Integer numberOfReviews;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private BigDecimal averageRating;

  private LocalDate startDate;

  private LocalDate endDate;

  private BigDecimal areaInMeters;

  private BigDecimal pricePerMeter;

  private MagazineType type;
}
