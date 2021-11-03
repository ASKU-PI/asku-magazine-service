package pl.asku.askumagazineservice.util.modelconverter;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.report.ReportDto;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.report.Report;

@Service
@AllArgsConstructor
public class ReportConverter {

  @Lazy
  MagazineConverter magazineConverter;
  @Lazy
  UserConverter userConverter;

  public ReportDto toDto(Report report) {
    return ReportDto.builder()
        .id(report.getId())
        .createdDate(report.getCreatedDate())
        .magazine(magazineConverter.toDto(report.getMagazine()))
        .reporter(userConverter.toDto(report.getReporter()))
        .body(report.getBody())
        .closed(report.getClosed())
        .build();
  }

  public Report toReport(ReportDto reportDto, User reporter, Magazine magazine) {
    return Report.builder()
        .body(reportDto.getBody())
        .reporter(reporter)
        .magazine(magazine)
        .build();
  }
}
