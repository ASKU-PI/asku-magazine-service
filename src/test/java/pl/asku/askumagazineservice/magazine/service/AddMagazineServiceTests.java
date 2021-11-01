package pl.asku.askumagazineservice.magazine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.repository.magazine.MagazineRepository;
import pl.asku.askumagazineservice.service.MagazineService;

public class AddMagazineServiceTests extends MagazineServiceTestBase {
  private final MagazineRepository magazineRepository;

  @Autowired
  AddMagazineServiceTests(MagazineService magazineService, MagazineRepository magazineRepository,
                          MagazineDataProvider magazineDataProvider,
                          ImageServiceClient imageServiceClient,
                          UserDataProvider userDataProvider) {
    super(magazineService, magazineDataProvider, imageServiceClient, userDataProvider);
    this.magazineRepository = magazineRepository;
  }

  @Test
  public void shouldAddToDatabase()
      throws LocationNotFoundException, LocationIqRequestFailedException {
    //given
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");

    //when
    Magazine magazine = magazineService.addMagazine(magazineDto, user, null);
    Optional<Magazine> magazineFromDb = magazineRepository.findById(magazine.getId());

    //then
    assertTrue(magazineFromDb.isPresent());
    Assertions.assertEquals(magazine.getId(), magazineFromDb.get().getId());
  }

  @Test
  public void shouldReturnCorrectMagazine()
      throws LocationNotFoundException, LocationIqRequestFailedException {
    //given
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");

    //when
    Magazine magazine = magazineService.addMagazine(magazineDto, user, null);

    //then
    Assertions.assertAll(
        () -> assertNotNull(magazine.getId()),
        () -> assertEquals(user, magazine.getOwner()),
        () -> assertNotNull(magazine.getCreatedDate()),
        () -> assertEquals(magazineDto.getCountry(), magazine.getCountry()),
        () -> assertEquals(magazineDto.getCity(), magazine.getCity()),
        () -> assertEquals(magazineDto.getStreet(), magazine.getStreet()),
        () -> assertEquals(magazineDto.getBuilding(), magazine.getBuilding()),
        () -> assertEquals(magazineDto.getStartDate(), magazine.getStartDate()),
        () -> assertEquals(magazineDto.getEndDate(), magazine.getEndDate()),
        () -> assertEquals(magazineDto.getAreaInMeters(), magazine.getAreaInMeters()),
        () -> assertEquals(magazineDto.getPricePerMeter(), magazine.getPricePerMeter()),
        () -> assertEquals(magazineDto.getType(), magazine.getType()),
        () -> assertEquals(magazineDto.getHeating(), magazine.getHeating()),
        () -> assertEquals(magazineDto.getLight(), magazine.getLight()),
        () -> assertEquals(magazineDto.getWhole(), magazine.getWhole()),
        () -> assertEquals(magazineDto.getMonitoring(), magazine.getMonitoring()),
        () -> assertEquals(magazineDto.getAntiTheftDoors(), magazine.getAntiTheftDoors()),
        () -> assertEquals(magazineDto.getVentilation(), magazine.getVentilation()),
        () -> assertEquals(magazineDto.getSmokeDetectors(), magazine.getSmokeDetectors()),
        () -> assertEquals(magazineDto.getSelfService(), magazine.getSelfService()),
        () -> assertEquals(magazineDto.getFloor(), magazine.getFloor()),
        () -> assertEquals(magazineDto.getHeight(), magazine.getHeight()),
        () -> assertEquals(magazineDto.getDoorHeight(), magazine.getDoorHeight()),
        () -> assertEquals(magazineDto.getDoorWidth(), magazine.getDoorWidth()),
        () -> assertEquals(magazineDto.getElectricity(), magazine.getElectricity()),
        () -> assertEquals(magazineDto.getParking(), magazine.getParking()),
        () -> assertEquals(magazineDto.getVehicleManoeuvreArea(),
            magazine.getVehicleManoeuvreArea()),
        () -> assertEquals(magazineDto.getMinAreaToRent(), magazine.getMinAreaToRent()),
        () -> assertEquals(magazineDto.getOwnerTransport(), magazine.getOwnerTransport()),
        () -> assertEquals(magazineDto.getDescription(), magazine.getDescription())
    );
  }

  @Test
  public void succeedsForOnlyMandatoryFields()
      throws LocationNotFoundException, LocationIqRequestFailedException {
    //given
    MagazineDto magazineDto = magazineDataProvider.mandatoryOnlyMagazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");

    //when
    Magazine magazine = magazineService.addMagazine(magazineDto, user, null);

    //then
    Assertions.assertAll(
        () -> assertNotNull(magazine.getId()),
        () -> assertEquals(user, magazine.getOwner()),
        () -> assertNotNull(magazine.getCreatedDate()),
        () -> assertEquals(magazineDto.getCountry(), magazine.getCountry()),
        () -> assertEquals(magazineDto.getCity(), magazine.getCity()),
        () -> assertEquals(magazineDto.getStreet(), magazine.getStreet()),
        () -> assertEquals(magazineDto.getBuilding(), magazine.getBuilding()),
        () -> assertEquals(magazineDto.getStartDate(), magazine.getStartDate()),
        () -> assertEquals(magazineDto.getEndDate(), magazine.getEndDate()),
        () -> assertEquals(magazineDto.getAreaInMeters(), magazine.getAreaInMeters()),
        () -> assertEquals(magazineDto.getPricePerMeter(), magazine.getPricePerMeter()),
        () -> assertEquals(magazineDto.getType(), magazine.getType()),
        () -> assertEquals(magazineDto.getHeating(), magazine.getHeating()),
        () -> assertEquals(magazineDto.getLight(), magazine.getLight()),
        () -> assertEquals(magazineDto.getWhole(), magazine.getWhole()),
        () -> assertEquals(magazineDto.getMonitoring(), magazine.getMonitoring()),
        () -> assertEquals(magazineDto.getAntiTheftDoors(), magazine.getAntiTheftDoors()),
        () -> assertEquals(magazineDto.getVentilation(), magazine.getVentilation()),
        () -> assertEquals(magazineDto.getSmokeDetectors(), magazine.getSmokeDetectors()),
        () -> assertEquals(magazineDto.getSelfService(), magazine.getSelfService()),
        () -> assertEquals(magazineDto.getFloor(), magazine.getFloor()),
        () -> assertEquals(magazineDto.getHeight(), magazine.getHeight()),
        () -> assertEquals(magazineDto.getDoorHeight(), magazine.getDoorHeight()),
        () -> assertEquals(magazineDto.getDoorWidth(), magazine.getDoorWidth()),
        () -> assertEquals(magazineDto.getElectricity(), magazine.getElectricity()),
        () -> assertEquals(magazineDto.getParking(), magazine.getParking()),
        () -> assertEquals(magazineDto.getVehicleManoeuvreArea(),
            magazine.getVehicleManoeuvreArea()),
        () -> assertEquals(magazineDto.getMinAreaToRent(), magazine.getMinAreaToRent()),
        () -> assertEquals(magazineDto.getOwnerTransport(), magazine.getOwnerTransport()),
        () -> assertEquals(magazineDto.getDescription(), magazine.getDescription())
    );
  }

  @Test
  public void failsForEmptyMandatoryFields() {
    //given
    MagazineDto magazineDto = magazineDataProvider.mandatoryOnlyMagazineDto().toBuilder()
        .city("")
        .build();
    User user = userDataProvider.user("test@test.pl", "666666666");

    //when
    assertThrows(RuntimeException.class,
        () -> magazineService.addMagazine(magazineDto, user, null));
  }

  @Test
  public void failsForStartDateEqualsEndDate() {
    LocalDate date = LocalDate.now().plusDays(1);

    MagazineDto magazineDto = magazineDataProvider.mandatoryOnlyMagazineDto().toBuilder()
        .startDate(date)
        .endDate(date)
        .build();
    User user = userDataProvider.user("test@test.pl", "666666666");

    //when
    assertThrows(RuntimeException.class,
        () -> magazineService.addMagazine(magazineDto, user, null));
  }

  @Test
  public void failsForStartDateGreaterThanEndDate() {
    LocalDate date = LocalDate.now().plusDays(1);

    MagazineDto magazineDto = magazineDataProvider.mandatoryOnlyMagazineDto().toBuilder()
        .startDate(date.plusDays(1))
        .endDate(date)
        .build();
    User user = userDataProvider.user("test@test.pl", "666666666");

    //when
    assertThrows(RuntimeException.class,
        () -> magazineService.addMagazine(magazineDto, user, null));
  }

  @Test
  public void failsForMinAreaToRentGreaterThanTotalArea() {
    BigDecimal area = BigDecimal.valueOf(100.0f);
    BigDecimal minAreaToRent = BigDecimal.valueOf(150.0f);

    MagazineDto magazineDto = magazineDataProvider.mandatoryOnlyMagazineDto().toBuilder()
        .areaInMeters(area)
        .minAreaToRent(minAreaToRent)
        .build();
    User user = userDataProvider.user("test@test.pl", "666666666");

    //when
    assertThrows(RuntimeException.class,
        () -> magazineService.addMagazine(magazineDto, user, null));
  }

  @Test
  public void failsForMinAreaToRentNotPositive() {
    BigDecimal minAreaToRent = BigDecimal.valueOf(0.0f);

    MagazineDto magazineDto = magazineDataProvider.mandatoryOnlyMagazineDto().toBuilder()
        .minAreaToRent(minAreaToRent)
        .build();
    User user = userDataProvider.user("test@test.pl", "666666666");

    //when
    assertThrows(RuntimeException.class,
        () -> magazineService.addMagazine(magazineDto, user, null));
  }
}
