package pl.asku.askumagazineservice.review.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotAvailableException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.exception.ReviewAlreadyExistsException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.ReservationDataProvider;
import pl.asku.askumagazineservice.helpers.data.ReviewDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.Review;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.reservation.Reservation;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;
import pl.asku.askumagazineservice.service.ReviewService;

public class AddReviewServiceTests extends ReviewServiceTestBase {

  @Autowired
  public AddReviewServiceTests(MagazineService magazineService,
                               MagazineDataProvider magazineDataProvider,
                               ReservationService reservationService, ReviewService reviewService,
                               UserDataProvider userDataProvider,
                               ReservationDataProvider reservationDataProvider,
                               ReviewDataProvider reviewDataProvider) {
    super(magazineService, magazineDataProvider, reservationService, reviewService,
        userDataProvider,
        reservationDataProvider, reviewDataProvider);
  }

  @Test
  public void returnsCorrectReview()
      throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotAvailableException, MagazineNotFoundException, ReviewAlreadyExistsException {
    //given
    User magazineOwner = userDataProvider.user("owner@test.pl", "666666666");
    Magazine magazine = magazineDataProvider.magazine(magazineOwner);
    User reserving = userDataProvider.user("reserving@test.pl", "777777777");
    Reservation reservation = reservationDataProvider.reservation(reserving, magazine);

    Review review = Review.builder()
        .body("test review")
        .rating(3)
        .reservation(reservation)
        .build();

    //when
    Review returnedReview = reviewService.addReview(review);

    //then
    assertEquals(review.getBody(), returnedReview.getBody());
    assertEquals(review.getRating(), returnedReview.getRating());
    assertEquals(review.getReservation(), returnedReview.getReservation());
    assertNotNull(returnedReview.getId());
  }

  @Test
  public void failsAddingTwice() throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotAvailableException, MagazineNotFoundException, ReviewAlreadyExistsException {
    //given
    User magazineOwner = userDataProvider.user("owner@test.pl", "666666666");
    Magazine magazine = magazineDataProvider.magazine(magazineOwner);
    User reserving = userDataProvider.user("reserving@test.pl", "777777777");
    Reservation reservation = reservationDataProvider.reservation(reserving, magazine);

    Review review = Review.builder()
        .body("test review")
        .rating(3)
        .reservation(reservation)
        .build();

    reviewService.addReview(review);

    //when then
    assertThrows(ReviewAlreadyExistsException.class, () -> reviewService.addReview(review));
  }

  @Test
  public void failsForNotPersistedReservation()
      throws LocationNotFoundException, LocationIqRequestFailedException,
      ReviewAlreadyExistsException {
    //given
    User magazineOwner = userDataProvider.user("owner@test.pl", "666666666");
    Magazine magazine = magazineDataProvider.magazine(magazineOwner);
    User reserving = userDataProvider.user("reserving@test.pl", "777777777");
    Reservation reservation = Reservation.builder()
        .areaInMeters(magazine.getMinAreaToRent())
        .startDate(magazine.getStartDate())
        .endDate(magazine.getEndDate())
        .magazine(magazine)
        .userId(reserving.getId())
        .build();

    Review review = Review.builder()
        .body("test review")
        .rating(3)
        .reservation(reservation)
        .build();

    //when then
    assertThrows(InvalidDataAccessApiUsageException.class, () -> reviewService.addReview(review));
  }
}
