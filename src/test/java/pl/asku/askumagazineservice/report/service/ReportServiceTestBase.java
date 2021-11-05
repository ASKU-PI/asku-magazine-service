package pl.asku.askumagazineservice.report.service;

import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.magazine.service.MagazineServiceTestBase;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReportService;

public class ReportServiceTestBase extends MagazineServiceTestBase {

  ReportService reportService;

  @Autowired
  public ReportServiceTestBase(MagazineService magazineService,
                               MagazineDataProvider magazineDataProvider,
                               ImageServiceClient imageServiceClient,
                               UserDataProvider userDataProvider,
                               ReportService reportService) {
    super(magazineService, magazineDataProvider, imageServiceClient, userDataProvider);
    this.reportService = reportService;
  }
}
