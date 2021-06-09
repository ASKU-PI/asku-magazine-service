package pl.asku.askumagazineservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pl.asku.askumagazineservice.model.MagazineType;

import java.time.LocalDate;

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

    private String location;

    private LocalDate startDate;

    private LocalDate endDate;

    private Float areaInMeters;

    private Float pricePerMeter;

    private MagazineType type;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Float freeSpace;
}
