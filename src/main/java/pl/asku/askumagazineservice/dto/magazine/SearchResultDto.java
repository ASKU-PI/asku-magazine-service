package pl.asku.askumagazineservice.dto.magazine;

import lombok.*;
import pl.asku.askumagazineservice.model.magazine.Geolocation;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDto {
    private List<MagazinePreviewDto> spaces;
    private Geolocation mapCenter;
    private Integer pages;
    private Integer records;
}
