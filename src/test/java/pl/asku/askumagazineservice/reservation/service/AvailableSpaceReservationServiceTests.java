package pl.asku.askumagazineservice.reservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;

public class AvailableSpaceReservationServiceTests extends ReservationServiceTestBase {

  @Autowired
  public AvailableSpaceReservationServiceTests(MagazineService magazineService,
                                               MagazineDataProvider magazineDataProvider,
                                               ReservationService reservationService,
                                               UserDataProvider userDataProvider,
                                               ReservationDataProvider reservationDataProvider) {
    super(magazineService, magazineDataProvider, reservationService, userDataProvider,
        reservationDataProvider);
  }

  @Test
  public void returnsWholeSpaceWhenNoOtherReservations() throws LocationNotFoundException,
      LocationIqRequestFailedException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
    LocalDate startDate = magazine.getStartDate().plusDays(1);
    LocalDate endDate = magazine.getEndDate().minusDays(1);

    //when
    BigDecimal availableArea = reservationService.getAvailableArea(
        magazine,
        startDate,
        endDate
    );

    //then
    assertEquals(availableArea, magazine.getAreaInMeters());
  }

  @Test
  public void returnsZeroWhenWholeSpaceReserved() throws LocationNotFoundException,
      LocationIqRequestFailedException, MagazineNotAvailableException, MagazineNotFoundException {
    //given
    MagazineCreateDto magazineDto = magazineDataProvider.magazineCreateDto().toBuilder().build();
    User user = userDataProvider.user("test@test.pl", "666666666");
    User otherUser = userDataProvider.user("test2@test.pl", "7777778777");
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
    BigDecimal availableArea = reservationService.getAvailableArea(
        magazine,
        startDate,
        endDate
    );

    //then
    assertEquals(0, BigDecimal.ZERO.compareTo(availableArea));
  }
}
