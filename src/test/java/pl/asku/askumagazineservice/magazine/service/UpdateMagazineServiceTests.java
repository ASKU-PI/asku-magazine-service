package pl.asku.askumagazineservice.magazine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.service.MagazineService;

public class UpdateMagazineServiceTests extends MagazineServiceTestBase {

  @Autowired
  public UpdateMagazineServiceTests(
      MagazineService magazineService,
      MagazineDataProvider magazineDataProvider,
      ImageServiceClient imageServiceClient,
      UserDataProvider userDataProvider) {
    super(magazineService, magazineDataProvider, imageServiceClient, userDataProvider);
  }

  @Test
  public void shouldUpdateMagazine()
      throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotFoundException {
    //given
    User owner = userDataProvider.user("owner@test.pl", "666666666");
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder()
        .title("Old title")
        .build();
    Magazine magazine = magazineDataProvider.magazine(owner, magazineDto);

    MagazineDto updatedMagazineDto = magazineDto.toBuilder()
        .title("Updated title")
        .build();

    //when
    Magazine updatedMagazine = magazineService.updateMagazine(
        magazine, updatedMagazineDto, null, null);

    Magazine reloadedMagazine = magazineService.getMagazine(magazine.getId());

    //then
    assertEquals(updatedMagazine.getTitle(), "Updated title");
    assertEquals(updatedMagazine.getId(), magazine.getId());

    assertEquals(reloadedMagazine.getTitle(), "Updated title");
    assertEquals(reloadedMagazine.getId(), magazine.getId());
  }
}
