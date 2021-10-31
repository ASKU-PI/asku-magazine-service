package pl.asku.askumagazineservice.model.magazine.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.asku.askumagazineservice.model.magazine.Geolocation;
import pl.asku.askumagazineservice.model.magazine.Magazine;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class SearchResult {
    private List<Magazine> spaces;
    private Geolocation mapCenter;
    private Integer pages;
    private Integer records;
}
