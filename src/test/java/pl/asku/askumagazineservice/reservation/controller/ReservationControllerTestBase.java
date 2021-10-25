package pl.asku.askumagazineservice.reservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.controller.ReservationController;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.reservation.service.ReservationServiceTestBase;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;

public class ReservationControllerTestBase extends ReservationServiceTestBase {

    protected final ReservationController reservationController;

    @Autowired
    public ReservationControllerTestBase(MagazineService magazineService, MagazineDataProvider magazineDataProvider,
                                         ReservationService reservationService,
                                         ReservationController reservationController,
                                         UserDataProvider userDataProvider) {
        super(magazineService, magazineDataProvider, reservationService, userDataProvider);
        this.reservationController = reservationController;
    }
}
