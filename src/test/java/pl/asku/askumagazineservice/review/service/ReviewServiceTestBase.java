package pl.asku.askumagazineservice.review.service;

import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.reservation.service.ReservationServiceTestBase;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;

public class ReviewServiceTestBase extends ReservationServiceTestBase {

    @Autowired
    public ReviewServiceTestBase(MagazineService magazineService, MagazineDataProvider magazineDataProvider, ReservationService reservationService, UserDataProvider userDataProvider) {
        super(magazineService, magazineDataProvider, reservationService, userDataProvider);
    }
}
