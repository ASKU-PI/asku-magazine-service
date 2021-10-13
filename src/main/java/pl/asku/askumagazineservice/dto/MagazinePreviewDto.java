package pl.asku.askumagazineservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pl.asku.askumagazineservice.dto.imageservice.PictureData;
import pl.asku.askumagazineservice.model.MagazineType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    private LocalDate createdDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<PictureData> photos;

    private String country;

    private String city;

    private String street;

    private String building;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal longitude;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal latitude;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal areaInMeters;

    private BigDecimal pricePerMeter;

    private MagazineType type;
}
