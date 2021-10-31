package pl.asku.askumagazineservice.review.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotAvailableException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.exception.ReservationNotFoundException;
import pl.asku.askumagazineservice.exception.ReviewAlreadyExistsException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.ReservationDataProvider;
import pl.asku.askumagazineservice.helpers.data.ReviewDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;
import pl.asku.askumagazineservice.service.ReviewService;

public class GetCountByMagazineReviewServiceTests extends ReviewServiceTestBase {

  @Autowired
  public GetCountByMagazineReviewServiceTests(MagazineService magazineService,
                                              MagazineDataProvider magazineDataProvider,
                                              ReservationService reservationService,
                                              ReviewService reviewService,
                                              UserDataProvider userDataProvider,
                                              ReservationDataProvider reservationDataProvider,
                                              ReviewDataProvider reviewDataProvider) {
    super(magazineService, magazineDataProvider, reservationService, reviewService,
        userDataProvider,
        reservationDataProvider, reviewDataProvider);
  }

  @Test
  public void returnsCorrectNumber()
      throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotAvailableException, MagazineNotFoundException, ReservationNotFoundException,
      ReviewAlreadyExistsException {
    //given
    User owner = userDataProvider.user("owner@test.pl", "666666666");
    Magazine magazine = magazineDataProvider.magazine(owner);

    User reserving1 = userDataProvider.user("reserving1@test.pl", "777777777");
    Reservation reservation1 = reservationDataProvider.reservation(reserving1, magazine);
    reviewDataProvider.review(reservation1);

    User reserving2 = userDataProvider.user("reserving2@test.pl", "888888888");
    Reservation reservation2 = reservationDataProvider.reservation(reserving2, magazine);
    reviewDataProvider.review(reservation2);

    //when
    Integer reviewCount = reviewService.getMagazineReviewsNumber(magazine.getId());

    //then
    assertEquals(reviewCount, 2);
  }

  @Test
  public void returnsZeroWhenNoReviews()
      throws LocationNotFoundException, LocationIqRequestFailedException {
    //given
    User owner = userDataProvider.user("owner@test.pl", "666666666");
    Magazine magazine = magazineDataProvider.magazine(owner);

    //when
    Integer reviewCount = reviewService.getMagazineReviewsNumber(magazine.getId());

    //then
    assertEquals(reviewCount, 0);
  }
}
