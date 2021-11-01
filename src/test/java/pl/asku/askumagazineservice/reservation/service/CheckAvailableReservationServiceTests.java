package pl.asku.askumagazineservice.reservation.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotAvailableException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.ReservationDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;

public class CheckAvailableReservationServiceTests extends ReservationServiceTestBase {

  @Autowired
  public CheckAvailableReservationServiceTests(MagazineService magazineService,
                                               MagazineDataProvider magazineDataProvider,
                                               ReservationService reservationService,
                                               UserDataProvider userDataProvider,
                                               ReservationDataProvider reservationDataProvider) {
    super(magazineService, magazineDataProvider, reservationService, userDataProvider,
        reservationDataProvider);
  }

  @Test
  public void returnsTrueWhenNoOtherReservations() throws LocationNotFoundException,
      LocationIqRequestFailedException {
    //given
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0f));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().plusDays(1);
    LocalDate endDate = magazine.getEndDate().minusDays(1);

    //when
    boolean available = reservationService.checkIfMagazineAvailable(
        magazine,
        startDate,
        endDate,
        area
    );

    //then
    assertTrue(available);
  }

  @Test
  public void returnsTrueWhenFullDateIntervalAndArea() throws LocationNotFoundException,
      LocationIqRequestFailedException {
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal area = magazineDto.getAreaInMeters();
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().plusDays(1);
    LocalDate endDate = magazine.getEndDate().minusDays(1);

    //when
    boolean available = reservationService.checkIfMagazineAvailable(
        magazine,
        startDate,
        endDate,
        area
    );

    //then
    assertTrue(available);
  }

  @Test
  public void returnsTrueWhenOneDayAndMinimumArea() throws LocationNotFoundException,
      LocationIqRequestFailedException {
    //given
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal area = magazineDto.getMinAreaToRent();
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate();
    LocalDate endDate = magazine.getStartDate().plusDays(1);

    //when
    boolean available = reservationService.checkIfMagazineAvailable(
        magazine,
        startDate,
        endDate,
        area
    );

    //then
    assertTrue(available);
  }

  @Test
  public void returnsFalseMagazineTooSmall()
      throws LocationNotFoundException, LocationIqRequestFailedException {
    //given
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal area = magazineDto.getAreaInMeters().add(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().plusDays(1);
    LocalDate endDate = magazine.getEndDate().minusDays(1);

    //when
    boolean available = reservationService.checkIfMagazineAvailable(
        magazine,
        startDate,
        endDate,
        area
    );

    //then
    assertFalse(available);
  }

  @Test
  public void returnsFalseWhenStartDateSmallerThanMagazineStartDate()
      throws LocationNotFoundException,
      LocationIqRequestFailedException {
    //given
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().minusDays(1);
    LocalDate endDate = magazine.getEndDate();

    //when
    boolean available = reservationService.checkIfMagazineAvailable(
        magazine,
        startDate,
        endDate,
        area
    );

    //then
    assertFalse(available);
  }

  @Test
  public void returnsFalseWhenEndDateGreaterThanMagazineStartDate()
      throws LocationNotFoundException,
      LocationIqRequestFailedException {
    //given
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate();
    LocalDate endDate = magazine.getEndDate().plusDays(1);

    //when
    boolean available = reservationService.checkIfMagazineAvailable(
        magazine,
        startDate,
        endDate,
        area
    );

    //then
    assertFalse(available);
  }

  @Test
  public void returnsFalseWhenStartDateLessThanMagazineStartAndEndDateGreaterThanMagazineStart()
      throws LocationNotFoundException, LocationIqRequestFailedException {
    //given
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().minusDays(1);
    LocalDate endDate = magazine.getEndDate().plusDays(1);

    //when
    boolean available = reservationService.checkIfMagazineAvailable(
        magazine,
        startDate,
        endDate,
        area
    );

    //then
    assertFalse(available);
  }

  @Test
  public void returnsFalseWhenStartDateGreaterThanMagazineEndDate()
      throws LocationNotFoundException,
      LocationIqRequestFailedException {
    //given
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getEndDate().minusDays(1);
    LocalDate endDate = magazine.getStartDate().plusDays(1);

    //when then
    assertThrows(ValidationException.class, () -> reservationService.checkIfMagazineAvailable(
        magazine,
        startDate,
        endDate,
        area
    ));
  }

  @Test
  public void succeedeWhenStartDateEqualsEndDate() throws LocationNotFoundException,
      LocationIqRequestFailedException {
    //given
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate();
    LocalDate endDate = magazine.getStartDate();

    //when
    boolean available = reservationService.checkIfMagazineAvailable(
        magazine,
        startDate,
        endDate,
        area
    );

    //then
    assertTrue(available);
  }

  @Test
  public void returnsFalseWhenAreaSmallerThanMinArea() throws LocationNotFoundException,
      LocationIqRequestFailedException {
    //given
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal area = magazineDto.getMinAreaToRent().subtract(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().plusDays(1);
    LocalDate endDate = magazine.getEndDate().minusDays(1);

    //when
    boolean available = reservationService.checkIfMagazineAvailable(
        magazine,
        startDate,
        endDate,
        area
    );

    //then
    assertFalse(available);
  }

  @Test
  public void returnsFalseWhenDatesIntervalCrossesOtherReservationAndAvailableAreaIsNotEnough()
      throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotAvailableException, MagazineNotFoundException {
    //given
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    User otherUser = userDataProvider.user("test2@test.pl", "777777777");
    BigDecimal area = magazineDto.getAreaInMeters();
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().plusDays(1);
    LocalDate endDate = magazine.getEndDate().minusDays(1);

    reservationService.addReservation(
        ReservationDto.builder()
            .startDate(startDate.minusDays(1))
            .endDate(endDate.plusDays(1))
            .areaInMeters(area)
            .magazineId(magazine.getId())
            .build(),
        otherUser
    );

    //when
    boolean available = reservationService.checkIfMagazineAvailable(
        magazine,
        startDate,
        endDate,
        area
    );

    //then
    assertFalse(available);
  }

  @Test
  public void returnsTrueWhenDatesIntervalCrossesOtherReservationAndAvailableAreaIsEnough()
      throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotAvailableException, MagazineNotFoundException {
    //given
    MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    User otherUser = userDataProvider.user("test2@test.pl", "777777777");
    BigDecimal area = magazineDto.getMinAreaToRent();
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().plusDays(1);
    LocalDate endDate = magazine.getEndDate().minusDays(1);
    reservationService.addReservation(
        ReservationDto.builder()
            .startDate(startDate.minusDays(1))
            .endDate(endDate.plusDays(1))
            .areaInMeters(area)
            .magazineId(magazine.getId())
            .build(),
        otherUser
    );

    //when
    boolean available = reservationService.checkIfMagazineAvailable(
        magazine,
        startDate,
        endDate,
        area
    );

    //then
    assertTrue(available);
  }
}
