package pl.asku.askumagazineservice.review.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import pl.asku.askumagazineservice.model.review.Review;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;
import pl.asku.askumagazineservice.service.ReviewService;

public class GetAverageByMagazineReviewServiceTests extends ReviewServiceTestBase {

  @Autowired
  public GetAverageByMagazineReviewServiceTests(MagazineService magazineService,
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
    Review review1 = reviewDataProvider.review(reservation1);

    User reserving2 = userDataProvider.user("reserving2@test.pl", "888888888");
    Reservation reservation2 = reservationDataProvider.reservation(reserving2, magazine);
    Review review2 = reviewDataProvider.review(reservation2);

    User reserving3 = userDataProvider.user("reserving3@test.pl", "999999999");
    Reservation reservation3 = reservationDataProvider.reservation(reserving3, magazine);
    Review review3 = reviewDataProvider.review(reservation3);

    //when
    BigDecimal averageRating = reviewService.getMagazineAverageRating(magazine.getId());

    //then
    BigDecimal expectedAverageRating = BigDecimal.valueOf(review1.getRating())
        .add(BigDecimal.valueOf(review2.getRating()))
        .add(BigDecimal.valueOf(review3.getRating()))
        .divide(BigDecimal.valueOf(3), 1, RoundingMode.HALF_EVEN);

    assertEquals(averageRating, expectedAverageRating);
  }

  @Test
  public void returnsNullWhenNoReviews()
      throws LocationNotFoundException, LocationIqRequestFailedException {
    //given
    User owner = userDataProvider.user("owner@test.pl", "666666666");
    Magazine magazine = magazineDataProvider.magazine(owner);

    //when
    BigDecimal averageRating = reviewService.getMagazineAverageRating(magazine.getId());

    //then
    assertNull(averageRating);
  }
}
