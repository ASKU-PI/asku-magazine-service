package pl.asku.askumagazineservice.dto.magazine;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.asku.askumagazineservice.model.magazine.Geolocation;

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
