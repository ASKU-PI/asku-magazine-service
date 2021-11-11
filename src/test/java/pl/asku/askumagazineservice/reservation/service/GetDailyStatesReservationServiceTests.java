package pl.asku.askumagazineservice.reservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.dto.magazine.MagazineCreateDto;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.dto.reservation.DailyStateDto;
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
import pl.asku.askumagazineservice.model.reservation.AvailabilityState;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;

public class GetDailyStatesReservationServiceTests extends ReservationServiceTestBase {

  @Autowired
  public GetDailyStatesReservationServiceTests(MagazineService magazineService,
                                               MagazineDataProvider magazineDataProvider,
                                               ReservationService reservationService,
                                               UserDataProvider userDataProvider,
                                               ReservationDataProvider reservationDataProvider) {
    super(magazineService, magazineDataProvider, reservationService, userDataProvider,
        reservationDataProvider);
  }

  @Test
  public void returnsCorrectResultMultipleDays()
      throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotAvailableException, MagazineNotFoundException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder()
        .startDate(LocalDate.now())
        .endDate(LocalDate.now().plusDays(2)).build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    User reservingUser = userDataProvider.user("test2@test.pl", "777777777");
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);

    reservationService.addReservation(
        ReservationDto.builder()
            .startDate(magazine.getStartDate())
            .endDate(magazine.getStartDate())
            .areaInMeters(magazine.getAreaInMeters())
            .magazineId(magazine.getId())
            .build(),
        reservingUser
    );

    reservationService.addReservation(
        ReservationDto.builder()
            .startDate(magazine.getStartDate().plusDays(1))
            .endDate(magazine.getStartDate().plusDays(1))
            .areaInMeters(magazine.getMinAreaToRent())
            .magazineId(magazine.getId())
            .build(),
        reservingUser
    );

    reservationService.addReservation(
        ReservationDto.builder()
            .startDate(magazine.getStartDate().plusDays(1))
            .endDate(magazine.getStartDate().plusDays(1))
            .areaInMeters(magazine.getMinAreaToRent())
            .magazineId(magazine.getId())
            .build(),
        reservingUser
    );

    Magazine otherMagazine = magazineDataProvider.magazine(user, magazineDto);

    reservationService.addReservation(
        ReservationDto.builder()
            .startDate(otherMagazine.getStartDate().plusDays(2))
            .endDate(otherMagazine.getStartDate().plusDays(2))
            .areaInMeters(otherMagazine.getAreaInMeters())
            .magazineId(otherMagazine.getId())
            .build(),
        reservingUser
    );

    //when
    List<DailyStateDto> states =
        reservationService.getDailyStates(magazine.getId(), magazine.getStartDate().minusDays(1),
            magazine.getEndDate().plusDays(2));

    //then
    assertEquals(states.size(), 6);

    assertEquals(states.get(0).getDay(), magazine.getStartDate().minusDays(1));
    assertEquals(states.get(0).getAvailabilityState(), AvailabilityState.UNAVAILABLE);

    assertEquals(states.get(1).getDay(), magazine.getStartDate());
    assertEquals(states.get(1).getAvailabilityState(), AvailabilityState.FULL);

    assertEquals(states.get(2).getDay(), magazine.getStartDate().plusDays(1));
    assertEquals(states.get(2).getAvailabilityState(), AvailabilityState.SOME);

    assertEquals(states.get(3).getDay(), magazine.getStartDate().plusDays(2));
    assertEquals(states.get(3).getAvailabilityState(), AvailabilityState.EMPTY);

    assertEquals(states.get(4).getDay(), magazine.getStartDate().plusDays(3));
    assertEquals(states.get(4).getAvailabilityState(), AvailabilityState.UNAVAILABLE);

    assertEquals(states.get(5).getDay(), magazine.getStartDate().plusDays(4));
    assertEquals(states.get(5).getAvailabilityState(), AvailabilityState.UNAVAILABLE);
  }
}
