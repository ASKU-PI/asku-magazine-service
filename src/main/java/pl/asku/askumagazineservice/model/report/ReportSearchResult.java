package pl.asku.askumagazineservice.model.report;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ReportSearchResult {
  private List<Report> reports;
  private Integer pages;
  private Long records;
}

