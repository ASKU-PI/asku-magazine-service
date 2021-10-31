package pl.asku.askumagazineservice.magazine.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Objects;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.controller.MagazineController;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.helpers.data.AuthenticationProvider;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.service.MagazineService;

public class GetMagazineControllerTests extends MagazineControllerTestBase {

  @Autowired
  public GetMagazineControllerTests(MagazineService magazineService,
                                    MagazineDataProvider magazineDataProvider,
                                    MagazineController magazineController,
                                    AuthenticationProvider authenticationProvider,
                                    ImageServiceClient imageServiceClient,
                                    UserDataProvider userDataProvider) {
    super(magazineService, magazineDataProvider, magazineController, authenticationProvider,
        imageServiceClient,
        userDataProvider);
  }

  @Test
  public void getMagazineDetailsShouldReturnCorrectMagazine()
      throws LocationIqRequestFailedException,
      LocationNotFoundException {
    //given
    User user = userDataProvider.user("test@test.pl", "666666666");
    Magazine magazine = magazineDataProvider.magazine(user);

    //when
    ResponseEntity<Object> response = magazineController.getMagazineDetails(magazine.getId());

    //then
    assertEquals(response.getStatusCode(), HttpStatus.OK);
    assertNotNull(response.getBody());
    assertEquals(Objects.requireNonNull(response.getBody()).getClass(), MagazineDto.class);
    MagazineDto responseBody = (MagazineDto) response.getBody();
    Assertions.assertEquals(magazine.getId(), responseBody.getId());
  }

  @Test
  public void failsWhenIdNull() {
    //when then
    assertThrows(ConstraintViolationException.class,
        () -> magazineController.getMagazineDetails(null));
  }
}
