package pl.asku.askumagazineservice.dto.review;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSearchResultDto {
  private List<ReviewDto> reviews;
  private Integer pages;
  private Long records;
}