package pl.asku.askumagazineservice.util.modelconverter;

import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.magazine.SearchResultDto;
import pl.asku.askumagazineservice.model.magazine.search.SearchResult;

@Service
@AllArgsConstructor
public class SearchResultConverter {

  @Lazy
  private final MagazineConverter magazineConverter;

  public SearchResultDto toDto(SearchResult searchResult) {
    return SearchResultDto.builder()
        .spaces(searchResult.getSpaces().stream().map(magazineConverter::toPreviewDto)
            .collect(Collectors.toList()))
        .mapCenter(searchResult.getMapCenter())
        .pages(searchResult.getPages())
        .records(searchResult.getRecords())
        .build();
  }

}
