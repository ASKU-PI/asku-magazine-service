package pl.asku.askumagazineservice.service;

import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.asku.askumagazineservice.dto.report.ReportDto;
import pl.asku.askumagazineservice.exception.ReportNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.report.Report;
import pl.asku.askumagazineservice.model.report.ReportSearchResult;
import pl.asku.askumagazineservice.repository.ReportRepository;
import pl.asku.askumagazineservice.util.modelconverter.ReportConverter;

@Service
@Validated
@AllArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;
  private final ReportConverter reportConverter;

  public Report addReport(
      @Valid ReportDto reportDto,
      @Valid User reporter,
      @Valid Magazine magazine) {
    Report report = reportConverter.toReport(reportDto, reporter, magazine);
    return reportRepository.save(report);
  }

  public Report getReport(@NotNull Long id) throws ReportNotFoundException {
    Optional<Report> report = reportRepository.findById(id);
    if (report.isEmpty()) {
      throw new ReportNotFoundException();
    }
    return report.get();
  }

  public ReportSearchResult getOpenReports(@NotNull Integer page) {
    Page<Report> reports = reportRepository
        .findAllByClosed(false, PageRequest.of(page - 1, 30));
    return ReportSearchResult.builder()
        .reports(reports.getContent())
        .pages(reports.getTotalPages())
        .records(reports.getTotalElements())
        .build();
  }

  public ReportSearchResult getClosedReports(@NotNull Integer page) {
    Page<Report> reports = reportRepository
        .findAllByClosed(true, PageRequest.of(page - 1, 30));
    return ReportSearchResult.builder()
        .reports(reports.getContent())
        .pages(reports.getTotalPages())
        .records(reports.getTotalElements())
        .build();
  }

  public Report closeReport(@Valid Report report) {
    report.setClosed(true);
    return reportRepository.save(report);
  }

  public Report reopenReport(@Valid Report report) {
    report.setClosed(false);
    return reportRepository.save(report);
  }

}
