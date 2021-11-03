package pl.asku.askumagazineservice.util.modelconverter;

import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.magazine.MagazineSearchResultDto;
import pl.asku.askumagazineservice.dto.report.ReportSearchResultDto;
import pl.asku.askumagazineservice.dto.review.ReviewSearchResultDto;
import pl.asku.askumagazineservice.model.magazine.search.MagazineSearchResult;
import pl.asku.askumagazineservice.model.report.ReportSearchResult;
import pl.asku.askumagazineservice.model.review.ReviewSearchResult;

@Service
@AllArgsConstructor
public class SearchResultConverter {

  @Lazy
  private final MagazineConverter magazineConverter;
  @Lazy
  private final ReviewConverter reviewConverter;
  @Lazy
  private final ReportConverter reportConverter;

  public MagazineSearchResultDto toDto(MagazineSearchResult searchResult) {
    return MagazineSearchResultDto.builder()
        .spaces(searchResult.getSpaces().stream().map(magazineConverter::toPreviewDto)
            .collect(Collectors.toList()))
        .mapCenter(searchResult.getMapCenter())
        .pages(searchResult.getPages())
        .records(searchResult.getRecords())
        .build();
  }

  public ReviewSearchResultDto toDto(
      ReviewSearchResult reviewSearchResult) {
    return ReviewSearchResultDto.builder()
        .reviews(reviewSearchResult.getReviews().stream().map(reviewConverter::toDto)
            .collect(Collectors.toList()))
        .pages(reviewSearchResult.getPages())
        .records(reviewSearchResult.getRecords())
        .build();
  }

  public ReportSearchResultDto toDto(
      ReportSearchResult reportSearchResult) {
    return ReportSearchResultDto.builder()
        .reports(reportSearchResult.getReports().stream().map(reportConverter::toDto)
            .collect(Collectors.toList()))
        .pages(reportSearchResult.getPages())
        .records(reportSearchResult.getRecords())
        .build();
  }

}
