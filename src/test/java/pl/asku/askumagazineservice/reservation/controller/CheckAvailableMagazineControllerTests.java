package pl.asku.askumagazineservice.reservation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.asku.askumagazineservice.controller.ReservationController;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class CheckAvailableMagazineControllerTests extends ReservationControllerTestBase {

    @Autowired
    public CheckAvailableMagazineControllerTests(MagazineService magazineService,
                                                 MagazineDataProvider magazineDataProvider,
                                                 ReservationService reservationService,
                                                 ReservationController reservationController,
                                                 UserDataProvider userDataProvider) {
        super(magazineService, magazineDataProvider, reservationService, reservationController, userDataProvider);
    }

    @Test
    public void returnsTrueWhenAvailable() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
        User user = userDataProvider.user("test@test.pl", "666666666");
        BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0f));
        Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        //when
        ResponseEntity<Object> response = reservationController.magazineAvailable(
                magazine.getId(),
                startDate,
                endDate,
                area
        );

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals(Objects.requireNonNull(response.getBody()).getClass(), Boolean.class);
        Boolean responseResult = (Boolean) response.getBody();
        assertTrue(responseResult);
    }

    @Test
    public void returnsFalseWhenUnavailable() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
        User user = userDataProvider.user("test@test.pl", "666666666");
        BigDecimal area = magazineDto.getAreaInMeters().add(BigDecimal.valueOf(2.0f));
        Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        //when
        ResponseEntity<Object> response = reservationController.magazineAvailable(
                magazine.getId(),
                startDate,
                endDate,
                area
        );

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals(Objects.requireNonNull(response.getBody()).getClass(), Boolean.class);
        Boolean responseResult = (Boolean) response.getBody();
        assertFalse(responseResult);
    }

    @Test
    public void failsWhenStartDateGreaterThanMagazineEndDate()
            throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
        User user = userDataProvider.user("test@test.pl", "666666666");
        BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineDataProvider.magazine(user, magazineDto);
        LocalDate startDate = magazine.getEndDate().minusDays(1);
        LocalDate endDate = magazine.getStartDate().plusDays(1);

        //when
        ResponseEntity<Object> response = reservationController.magazineAvailable(
                magazine.getId(),
                startDate,
                endDate,
                area
        );

        //then
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
}
