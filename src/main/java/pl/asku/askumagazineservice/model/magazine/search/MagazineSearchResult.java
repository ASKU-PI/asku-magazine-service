package pl.asku.askumagazineservice.model.magazine.search;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.asku.askumagazineservice.model.magazine.Geolocation;
import pl.asku.askumagazineservice.model.magazine.Magazine;

@AllArgsConstructor
@Builder
@Getter
public class MagazineSearchResult {
  private List<Magazine> spaces;
  private Geolocation mapCenter;
  private Integer pages;
  private Integer records;
}
