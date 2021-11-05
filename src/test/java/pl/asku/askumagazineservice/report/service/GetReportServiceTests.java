package pl.asku.askumagazineservice.report.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.report.ReportDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.ReportNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.report.Report;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReportService;

public class GetReportServiceTests extends ReportServiceTestBase {

  @Autowired
  public GetReportServiceTests(MagazineService magazineService,
                               MagazineDataProvider magazineDataProvider,
                               ImageServiceClient imageServiceClient,
                               UserDataProvider userDataProvider,
                               ReportService reportService) {
    super(magazineService, magazineDataProvider, imageServiceClient, userDataProvider,
        reportService);
  }

  @Test
  public void returnsCorrectReport()
      throws LocationNotFoundException, LocationIqRequestFailedException, ReportNotFoundException {
    //given
    User owner = userDataProvider.user("owner@test.pl", "666666666");
    Magazine magazine = magazineDataProvider.magazine(owner);
    User reporter = userDataProvider.user("reporter@test.pl", "777777777");
    ReportDto reportDto = ReportDto.builder()
        .body("Test body")
        .build();
    Report addedReport = reportService.addReport(reportDto, reporter, magazine);

    //when
    Report report = reportService.getReport(addedReport.getId());

    //then
    assertEquals(reporter.getId(), report.getReporter().getId());
    assertEquals(magazine.getId(), report.getMagazine().getId());
    assertEquals(reportDto.getBody(), report.getBody());
  }
}
