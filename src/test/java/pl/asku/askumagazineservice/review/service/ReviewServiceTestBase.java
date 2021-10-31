package pl.asku.askumagazineservice.review.service;

import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.ReservationDataProvider;
import pl.asku.askumagazineservice.helpers.data.ReviewDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.reservation.service.ReservationServiceTestBase;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;
import pl.asku.askumagazineservice.service.ReviewService;

public class ReviewServiceTestBase extends ReservationServiceTestBase {

  protected final ReviewService reviewService;
  protected final ReservationDataProvider reservationDataProvider;
  protected final ReviewDataProvider reviewDataProvider;

  @Autowired
  public ReviewServiceTestBase(MagazineService magazineService,
                               MagazineDataProvider magazineDataProvider,
                               ReservationService reservationService, ReviewService reviewService,
                               UserDataProvider userDataProvider,
                               ReservationDataProvider reservationDataProvider,
                               ReviewDataProvider reviewDataProvider) {
    super(magazineService, magazineDataProvider, reservationService, userDataProvider,
        reservationDataProvider);
    this.reviewService = reviewService;
    this.reservationDataProvider = reservationDataProvider;
    this.reviewDataProvider = reviewDataProvider;
  }
}
