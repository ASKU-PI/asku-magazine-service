package pl.asku.askumagazineservice.magazine.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.controller.MagazineController;
import pl.asku.askumagazineservice.dto.magazine.MagazineCreateDto;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.helpers.data.AuthenticationProvider;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.service.MagazineService;

public class AddMagazineControllerTests extends MagazineControllerTestBase {

  @Autowired
  public AddMagazineControllerTests(MagazineService magazineService,
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
  public void shouldReturnCorrectMagazine() {
    //given
    User user = userDataProvider.user("test@test.pl", "666666666");
    Authentication authentication = authenticationProvider.userAuthentication(user);
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();

    //when
    ResponseEntity<Object> response =
        magazineController.addMagazine(magazineDto, null, authentication);

    //then
    assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    assertNotNull(response.getBody());
    assertEquals(Objects.requireNonNull(response.getBody()).getClass(), MagazineDto.class);
    MagazineDto responseBody = (MagazineDto) response.getBody();

    Assertions.assertAll(
        () -> assertNotNull(responseBody.getId()),
        () -> assertEquals(responseBody.getOwner().getId(), authentication.getName()),
        () -> assertNotNull(responseBody.getCreatedDate()),
        () -> assertEquals(responseBody.getCountry(), magazineDto.getCountry()),
        () -> assertEquals(responseBody.getCity(), magazineDto.getCity()),
        () -> assertEquals(responseBody.getStreet(), magazineDto.getStreet()),
        () -> assertEquals(responseBody.getBuilding(), magazineDto.getBuilding()),
        () -> assertEquals(responseBody.getStartDate(), magazineDto.getStartDate()),
        () -> assertEquals(responseBody.getEndDate(), magazineDto.getEndDate()),
        () -> assertEquals(responseBody.getAreaInMeters(), magazineDto.getAreaInMeters()),
        () -> assertEquals(responseBody.getPricePerMeter(), magazineDto.getPricePerMeter()),
        () -> assertEquals(responseBody.getType(), magazineDto.getType()),
        () -> assertEquals(responseBody.getHeating(), magazineDto.getHeating()),
        () -> assertEquals(responseBody.getLight(), magazineDto.getLight()),
        () -> assertEquals(responseBody.getWhole(), magazineDto.getWhole()),
        () -> assertEquals(responseBody.getMonitoring(), magazineDto.getMonitoring()),
        () -> assertEquals(responseBody.getAntiTheftDoors(), magazineDto.getAntiTheftDoors()),
        () -> assertEquals(responseBody.getVentilation(), magazineDto.getVentilation()),
        () -> assertEquals(responseBody.getSmokeDetectors(), magazineDto.getSmokeDetectors()),
        () -> assertEquals(responseBody.getSelfService(), magazineDto.getSelfService()),
        () -> assertEquals(responseBody.getFloor(), magazineDto.getFloor()),
        () -> assertEquals(responseBody.getHeight(), magazineDto.getHeight()),
        () -> assertEquals(responseBody.getDoorHeight(), magazineDto.getDoorHeight()),
        () -> assertEquals(responseBody.getDoorWidth(), magazineDto.getDoorWidth()),
        () -> assertEquals(responseBody.getElectricity(), magazineDto.getElectricity()),
        () -> assertEquals(responseBody.getParking(), magazineDto.getParking()),
        () -> assertEquals(responseBody.getVehicleManoeuvreArea(),
            magazineDto.getVehicleManoeuvreArea()),
        () -> assertEquals(responseBody.getMinAreaToRent(), magazineDto.getMinAreaToRent()),
        () -> assertEquals(responseBody.getOwnerTransport(), magazineDto.getOwnerTransport()),
        () -> assertEquals(responseBody.getDescription(), magazineDto.getDescription())
    );
  }

  @Test
  public void failsForEmptyMandatoryFields() {
    //given
    User user = userDataProvider.user("test@test.pl", "666666666");
    Authentication authentication = authenticationProvider.userAuthentication(user);
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder()
        .city("")
        .build();

    //when then
    assertThrows(ConstraintViolationException.class,
        () -> magazineController.addMagazine(magazineDto, null,
            authentication));
  }

  @Test
  public void failsWhenNotAuthenticated() {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();

    //when
    ResponseEntity<Object> response = magazineController.addMagazine(magazineDto, null, null);

    //then
    assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
  }

  @Test
  public void failsWhenValidationNotSuccessful() {
    //given
    BigDecimal minAreaToRent = BigDecimal.valueOf(0.0f);

    User user = userDataProvider.user("test@test.pl", "666666666");
    Authentication authentication = authenticationProvider.userAuthentication(user);

    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder()
        .minAreaToRent(minAreaToRent)
        .build();

    //when then
    assertThrows(ConstraintViolationException.class,
        () -> magazineController.addMagazine(magazineDto, null,
            authentication));
  }

}
