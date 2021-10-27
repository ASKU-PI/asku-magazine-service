package pl.asku.askumagazineservice.reservation.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.dto.ReservationDto;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotAvailableException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.Reservation;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetDailyReservationServiceTests extends ReservationServiceTestBase{

    @Autowired
    public GetDailyReservationServiceTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider, ReservationService reservationService, UserDataProvider userDataProvider) {
        super(magazineService, magazineDataProvider, reservationService, userDataProvider);
    }

    @Test
    public void returnsCorrectReservations() throws LocationNotFoundException, LocationIqRequestFailedException, MagazineNotAvailableException, MagazineNotFoundException {
        //given
        int toBeReturnedReservationsNumber = 2;

        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = userDataProvider.getUser("test@test.pl").getId();
        String reserverUsername = userDataProvider.getUser("reserver@test.pl").getId();
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);

        IntStream.range(0, toBeReturnedReservationsNumber).forEach($ ->
        {
            try {
                reservationService.addReservation(
                    ReservationDto.builder()
                            .startDate(magazine.getStartDate())
                            .endDate(magazineDto.getEndDate().minusDays(1))
                            .areaInMeters(magazine.getMinAreaToRent())
                            .magazineId(magazine.getId())
                            .build(),
                        reserverUsername
                );
            } catch (MagazineNotAvailableException | MagazineNotFoundException e) {
                e.printStackTrace();
            }
        });

        reservationService.addReservation(
                ReservationDto.builder()
                        .startDate(magazine.getEndDate().minusDays(1))
                        .endDate(magazineDto.getEndDate())
                        .areaInMeters(magazine.getMinAreaToRent())
                        .magazineId(magazine.getId())
                        .build(),
                reserverUsername
        );

        //when
        List<Reservation> reservationList = reservationService.getDailyReservations(magazine.getId(), magazine.getStartDate());

        //then
        assertEquals(reservationList.size(), toBeReturnedReservationsNumber);
        reservationList.forEach(reservation -> {
            assertTrue(reservation.getStartDate().compareTo(magazine.getStartDate()) <= 0);
            assertTrue(reservation.getEndDate().compareTo(magazine.getStartDate()) >= 0);
            assertEquals(reservation.getMagazine().getId(), magazine.getId());
        });
    }
}
