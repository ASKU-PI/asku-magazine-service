package pl.asku.askumagazineservice.report.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.report.ReportDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.report.ReportSearchResult;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReportService;

public class GetOpenReportsTests extends ReportServiceTestBase {

  @Autowired
  public GetOpenReportsTests(MagazineService magazineService,
                             MagazineDataProvider magazineDataProvider,
                             ImageServiceClient imageServiceClient,
                             UserDataProvider userDataProvider,
                             ReportService reportService) {
    super(magazineService, magazineDataProvider, imageServiceClient, userDataProvider,
        reportService);
  }

  @Test
  public void returnsOpenReports()
      throws LocationNotFoundException, LocationIqRequestFailedException {
    //given
    User owner = userDataProvider.user("owner@test.pl", "666666666");
    Magazine magazine = magazineDataProvider.magazine(owner);
    User reporter = userDataProvider.user("reporter@test.pl", "777777777");
    ReportDto reportDto = ReportDto.builder()
        .body("Test body")
        .build();

    for (int i = 0; i < 5; i++) {
      reportService.addReport(reportDto, reporter, magazine);
    }

    //when
    ReportSearchResult searchResult = reportService.getOpenReports(1);

    //then
    assertEquals(searchResult.getRecords(), 5);
  }
}
