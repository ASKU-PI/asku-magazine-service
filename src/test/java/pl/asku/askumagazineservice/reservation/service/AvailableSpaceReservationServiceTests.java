package pl.asku.askumagazineservice.reservation.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotAvailableException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class AvailableSpaceReservationServiceTests extends ReservationServiceTestBase{

    @Autowired
    public AvailableSpaceReservationServiceTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider, ReservationService reservationService, UserDataProvider userDataProvider) {
        super(magazineService, magazineDataProvider, reservationService, userDataProvider);
    }

    @Test
    public void returnsWholeSpaceWhenNoOtherReservations() throws LocationNotFoundException,
            LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = userDataProvider.getUser("test@test.pl").getId();
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
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
    public void returnsZeroWhenWholeSpaceReserved() throws LocationNotFoundException, LocationIqRequestFailedException, MagazineNotAvailableException, MagazineNotFoundException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = userDataProvider.getUser("test@test.pl").getId();
        String otherUserIdentifier = userDataProvider.getUser("test2@test.pl").getId();
        BigDecimal area = magazineDto.getAreaInMeters();
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        reservationService.addReservation(
                ReservationDto.builder()
                        .startDate(startDate.minusDays(1))
                        .endDate(endDate.plusDays(1))
                        .areaInMeters(area)
                        .magazineId(magazine.getId())
                        .build(),
                otherUserIdentifier
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
