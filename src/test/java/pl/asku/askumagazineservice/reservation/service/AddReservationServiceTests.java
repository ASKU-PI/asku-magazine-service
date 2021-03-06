package pl.asku.askumagazineservice.reservation.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.dto.magazine.MagazineCreateDto;
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
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;

class AddReservationServiceTests extends ReservationServiceTestBase {

  @Autowired
  AddReservationServiceTests(MagazineService magazineService,
                             MagazineDataProvider magazineDataProvider,
                             ReservationService reservationService,
                             UserDataProvider userDataProvider,
                             ReservationDataProvider reservationDataProvider) {
    super(magazineService, magazineDataProvider, reservationService, userDataProvider,
        reservationDataProvider);
  }

  @Test
  public void returnsCorrectReservation()
      throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotAvailableException, MagazineNotFoundException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().plusDays(1);
    LocalDate endDate = magazine.getEndDate().minusDays(1);

    //when
    ReservationDto reservationDto = ReservationDto.builder()
        .startDate(startDate)
        .endDate(endDate)
        .areaInMeters(areaToRent)
        .magazineId(magazine.getId())
        .build();

    Reservation reservation = reservationService.addReservation(
        reservationDto,
        user
    );

    //then
    assertAll(
        () -> assertEquals(reservation.getStartDate(), reservationDto.getStartDate()),
        () -> assertEquals(reservation.getEndDate(), reservationDto.getEndDate()),
        () -> assertEquals(reservation.getAreaInMeters(), reservationDto.getAreaInMeters()),
        () -> assertEquals(reservation.getMagazine().getId(), reservationDto.getMagazineId())
    );
  }

  @Test
  public void reserveFullDateIntervalAndArea()
      throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotAvailableException, MagazineNotFoundException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal areaToRent = magazineDto.getAreaInMeters();
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate();
    LocalDate endDate = magazine.getEndDate();

    //when
    ReservationDto reservationDto = ReservationDto.builder()
        .startDate(startDate)
        .endDate(endDate)
        .areaInMeters(areaToRent)
        .magazineId(magazine.getId())
        .build();

    Reservation reservation = reservationService.addReservation(
        reservationDto,
        user
    );

    //then
    assertAll(
        () -> assertEquals(reservation.getStartDate(), reservationDto.getStartDate()),
        () -> assertEquals(reservation.getEndDate(), reservationDto.getEndDate()),
        () -> assertEquals(reservation.getAreaInMeters(), reservationDto.getAreaInMeters()),
        () -> assertEquals(reservation.getMagazine().getId(), reservationDto.getMagazineId())
    );
  }

  @Test
  public void reserveOneDayAndMinimumArea()
      throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotAvailableException, MagazineNotFoundException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal areaToRent = magazineDto.getMinAreaToRent();
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate();
    LocalDate endDate = magazine.getStartDate().plusDays(1);

    //when
    ReservationDto reservationDto = ReservationDto.builder()
        .startDate(startDate)
        .endDate(endDate)
        .areaInMeters(areaToRent)
        .magazineId(magazine.getId())
        .build();

    Reservation reservation = reservationService.addReservation(
        reservationDto,
        user
    );

    //then
    assertAll(
        () -> assertEquals(reservation.getStartDate(), reservationDto.getStartDate()),
        () -> assertEquals(reservation.getEndDate(), reservationDto.getEndDate()),
        () -> assertEquals(reservation.getAreaInMeters(), reservationDto.getAreaInMeters()),
        () -> assertEquals(reservation.getMagazine().getId(), reservationDto.getMagazineId())
    );
  }

  @Test
  public void failsForMagazineTooSmall()
      throws LocationNotFoundException, LocationIqRequestFailedException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal areaToRent = magazineDto.getAreaInMeters().add(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().plusDays(1);
    LocalDate endDate = magazine.getEndDate().minusDays(1);

    ReservationDto reservationDto = ReservationDto.builder()
        .startDate(startDate)
        .endDate(endDate)
        .areaInMeters(areaToRent)
        .magazineId(magazine.getId())
        .build();

    //when then
    assertThrows(MagazineNotAvailableException.class, () -> reservationService.addReservation(
        reservationDto,
        user
    ));
  }

  @Test
  public void failsWhenStartDateSmallerThanMagazineStartDate() throws LocationNotFoundException,
      LocationIqRequestFailedException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().minusDays(1);
    LocalDate endDate = magazine.getEndDate();

    ReservationDto reservationDto = ReservationDto.builder()
        .startDate(startDate)
        .endDate(endDate)
        .areaInMeters(areaToRent)
        .magazineId(magazine.getId())
        .build();

    //when then
    assertThrows(MagazineNotAvailableException.class, () -> reservationService.addReservation(
        reservationDto,
        user
    ));
  }

  @Test
  public void failsWhenEndDateGreaterThanMagazineStartDate() throws LocationNotFoundException,
      LocationIqRequestFailedException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate();
    LocalDate endDate = magazine.getEndDate().plusDays(1);

    ReservationDto reservationDto = ReservationDto.builder()
        .startDate(startDate)
        .endDate(endDate)
        .areaInMeters(areaToRent)
        .magazineId(magazine.getId())
        .build();

    //when then
    assertThrows(MagazineNotAvailableException.class, () -> reservationService.addReservation(
        reservationDto,
        user
    ));
  }

  @Test
  public void failsWhenStartDateSmallerThanMagazioneStartDateEndDateGreaterThanMagazineStartDate()
      throws LocationNotFoundException, LocationIqRequestFailedException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().minusDays(1);
    LocalDate endDate = magazine.getEndDate().plusDays(1);

    ReservationDto reservationDto = ReservationDto.builder()
        .startDate(startDate)
        .endDate(endDate)
        .areaInMeters(areaToRent)
        .magazineId(magazine.getId())
        .build();

    //when then
    assertThrows(MagazineNotAvailableException.class, () -> reservationService.addReservation(
        reservationDto,
        user
    ));
  }

  @Test
  public void failsWhenStartDateGreaterThanMagazineEndDate() throws LocationNotFoundException,
      LocationIqRequestFailedException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getEndDate().minusDays(1);
    LocalDate endDate = magazine.getStartDate().plusDays(2);

    ReservationDto reservationDto = ReservationDto.builder()
        .startDate(startDate)
        .endDate(endDate)
        .areaInMeters(areaToRent)
        .magazineId(magazine.getId())
        .build();

    //when then
    assertThrows(ValidationException.class, () -> reservationService.addReservation(
        reservationDto,
        user
    ));
  }

  @Test
  public void succeedsWhenStartDateEqualsEndDate() throws LocationNotFoundException,
      LocationIqRequestFailedException, MagazineNotAvailableException, MagazineNotFoundException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate();
    LocalDate endDate = magazine.getStartDate();

    ReservationDto reservationDto = ReservationDto.builder()
        .startDate(startDate)
        .endDate(endDate)
        .areaInMeters(areaToRent)
        .magazineId(magazine.getId())
        .build();

    //when
    Reservation reservation = reservationService.addReservation(
        reservationDto,
        user
    );

    // then
    assertAll(
        () -> assertEquals(reservation.getStartDate(), startDate),
        () -> assertEquals(reservation.getEndDate(), endDate),
        () -> assertEquals(reservation.getAreaInMeters(), areaToRent),
        () -> assertEquals(reservation.getMagazine().getId(), reservationDto.getMagazineId()));
  }

  @Test
  public void failsWhenAreaSmallerThanMinArea()
      throws LocationNotFoundException, LocationIqRequestFailedException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    BigDecimal areaToRent = magazineDto.getMinAreaToRent().subtract(BigDecimal.valueOf(2.0d));
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().plusDays(1);
    LocalDate endDate = magazine.getEndDate().minusDays(1);

    ReservationDto reservationDto = ReservationDto.builder()
        .startDate(startDate)
        .endDate(endDate)
        .areaInMeters(areaToRent)
        .magazineId(magazine.getId())
        .build();

    //when then
    assertThrows(MagazineNotAvailableException.class, () -> reservationService.addReservation(
        reservationDto,
        user
    ));
  }

  @Test
  public void failsWhenDatesIntervalCrossesOtherReservationAndAvailableAreaIsNotEnough()
      throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotAvailableException, MagazineNotFoundException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    User otherUser = userDataProvider.user("test2@test.pl", "777777");
    BigDecimal areaToRent = magazineDto.getAreaInMeters();
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().plusDays(1);
    LocalDate endDate = magazine.getEndDate().minusDays(1);

    reservationService.addReservation(
        ReservationDto.builder()
            .startDate(startDate.minusDays(1))
            .endDate(endDate.plusDays(1))
            .areaInMeters(areaToRent)
            .magazineId(magazine.getId())
            .build(),
        otherUser
    );

    //when then
    assertThrows(MagazineNotAvailableException.class, () -> reservationService.addReservation(
        ReservationDto.builder()
            .startDate(startDate)
            .endDate(endDate)
            .areaInMeters(areaToRent)
            .magazineId(magazine.getId())
            .build(),
        user
    ));
  }

  @Test
  public void succeedsWhenDatesIntervalCrossesOtherReservationAndAvailableAreaIsEnough()
      throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotAvailableException, MagazineNotFoundException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    User otherUser = userDataProvider.user("test2@test.pl", "777777");
    BigDecimal areaToRent = magazineDto.getMinAreaToRent();
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().plusDays(1);
    LocalDate endDate = magazine.getEndDate().minusDays(1);
    reservationService.addReservation(
        ReservationDto.builder()
            .startDate(startDate.minusDays(1))
            .endDate(endDate.plusDays(1))
            .areaInMeters(areaToRent)
            .magazineId(magazine.getId())
            .build(),
        otherUser
    );

    ReservationDto reservationDto = ReservationDto.builder()
        .startDate(startDate)
        .endDate(endDate)
        .areaInMeters(areaToRent)
        .magazineId(magazine.getId())
        .build();

    //when
    Reservation reservation = reservationService.addReservation(
        reservationDto,
        user
    );

    //then
    assertAll(
        () -> assertEquals(reservation.getStartDate(), reservationDto.getStartDate()),
        () -> assertEquals(reservation.getEndDate(), reservationDto.getEndDate()),
        () -> assertEquals(reservation.getAreaInMeters(), reservationDto.getAreaInMeters()),
        () -> assertEquals(reservation.getMagazine().getId(), reservationDto.getMagazineId())
    );
  }
}
