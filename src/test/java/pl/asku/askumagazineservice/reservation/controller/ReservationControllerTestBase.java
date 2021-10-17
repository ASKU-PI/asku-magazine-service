package pl.asku.askumagazineservice.reservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.controller.ReservationController;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.magazine.service.MagazineService;
import pl.asku.askumagazineservice.magazine.service.ReservationService;
import pl.asku.askumagazineservice.reservation.service.ReservationServiceTestBase;

public class ReservationControllerTestBase extends ReservationServiceTestBase {

    protected final ReservationController reservationController;

    @Autowired
    public ReservationControllerTestBase(MagazineService magazineService, MagazineDataProvider magazineDataProvider,
                                         ReservationService reservationService,
                                         ReservationController reservationController) {
        super(magazineService, magazineDataProvider, reservationService);
        this.reservationController = reservationController;
    }
}
