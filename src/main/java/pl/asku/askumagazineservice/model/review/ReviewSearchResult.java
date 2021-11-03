package pl.asku.askumagazineservice.model.review;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ReviewSearchResult {
  private List<Review> reviews;
  private Integer pages;
  private Long records;
}
