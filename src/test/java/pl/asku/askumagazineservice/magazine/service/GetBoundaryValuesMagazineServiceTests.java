package pl.asku.askumagazineservice.magazine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.magazine.MagazineBoundaryValuesDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.service.MagazineService;

public class GetBoundaryValuesMagazineServiceTests extends MagazineServiceTestBase {

  @Autowired
  public GetBoundaryValuesMagazineServiceTests(
      MagazineService magazineService,
      MagazineDataProvider magazineDataProvider,
      ImageServiceClient imageServiceClient,
      UserDataProvider userDataProvider) {
    super(magazineService, magazineDataProvider, imageServiceClient, userDataProvider);
  }

  @Test
  public void shouldReturnNullsWhenNoMagazines() {
    //when
    MagazineBoundaryValuesDto magazineBoundaryValuesDto = magazineService.getBoundaryValues();

    //then
    assertNull(magazineBoundaryValuesDto.getMaxArea());
    assertNull(magazineBoundaryValuesDto.getMinArea());
    assertNull(magazineBoundaryValuesDto.getMaxTemperature());
    assertNull(magazineBoundaryValuesDto.getMinTemperature());
  }

  @Test
  public void shoudReturnMagazineValuesWhenOnlyOneExists()
      throws LocationNotFoundException, LocationIqRequestFailedException {
    //given
    User owner = userDataProvider.user("owner@test.pl", "666666666");
    Magazine magazine = magazineDataProvider.magazine(owner);

    //when
    MagazineBoundaryValuesDto magazineBoundaryValuesDto = magazineService.getBoundaryValues();

    //then
    assertEquals(magazine.getAreaInMeters().compareTo(magazineBoundaryValuesDto.getMinArea()), 0);
    assertEquals(magazine.getAreaInMeters().compareTo(magazineBoundaryValuesDto.getMaxArea()), 0);
  }
}
