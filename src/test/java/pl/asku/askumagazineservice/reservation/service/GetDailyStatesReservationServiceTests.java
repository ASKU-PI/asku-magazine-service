package pl.asku.askumagazineservice.reservation.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.dto.reservation.DailyStateDto;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotAvailableException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.reservation.AvailabilityState;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetDailyStatesReservationServiceTests extends ReservationServiceTestBase {

    @Autowired
    public GetDailyStatesReservationServiceTests(MagazineService magazineService,
                                                 MagazineDataProvider magazineDataProvider,
                                                 ReservationService reservationService,
                                                 UserDataProvider userDataProvider) {
        super(magazineService, magazineDataProvider, reservationService, userDataProvider);
    }

    @Test
    public void returnsCorrectResultMultipleDays() throws LocationNotFoundException, LocationIqRequestFailedException
            , MagazineNotAvailableException, MagazineNotFoundException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = userDataProvider.getUser("test@test.pl").getId();
        String reservingUsername = userDataProvider.getUser("reserving@test.pl").getId();
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);

        reservationService.addReservation(
                ReservationDto.builder()
                        .startDate(magazine.getStartDate())
                        .endDate(magazine.getStartDate())
                        .areaInMeters(magazine.getAreaInMeters())
                        .magazineId(magazine.getId())
                        .build(),
                reservingUsername
        );

        reservationService.addReservation(
                ReservationDto.builder()
                        .startDate(magazine.getStartDate().plusDays(1))
                        .endDate(magazine.getStartDate().plusDays(1))
                        .areaInMeters(magazine.getMinAreaToRent())
                        .magazineId(magazine.getId())
                        .build(),
                reservingUsername
        );

        reservationService.addReservation(
                ReservationDto.builder()
                        .startDate(magazine.getStartDate().plusDays(1))
                        .endDate(magazine.getStartDate().plusDays(1))
                        .areaInMeters(magazine.getMinAreaToRent())
                        .magazineId(magazine.getId())
                        .build(),
                reservingUsername
        );

        reservationService.addReservation(
                ReservationDto.builder()
                        .startDate(magazine.getEndDate())
                        .endDate(magazine.getEndDate())
                        .areaInMeters(magazine.getAreaInMeters())
                        .magazineId(magazine.getId())
                        .build(),
                reservingUsername
        );

        //when
        List<DailyStateDto> states = reservationService.getDailyStates(magazine.getId(), magazine.getStartDate(),
                magazine.getStartDate().plusDays(2));

        //then
        assertEquals(states.size(), 3);

        assertEquals(states.get(0).getDay(), magazine.getStartDate());
        assertEquals(states.get(0).getAvailabilityState(), AvailabilityState.FULL);

        assertEquals(states.get(1).getDay(), magazine.getStartDate().plusDays(1));
        assertEquals(states.get(1).getAvailabilityState(), AvailabilityState.SOME);

        assertEquals(states.get(2).getDay(), magazine.getStartDate().plusDays(2));
        assertEquals(states.get(2).getAvailabilityState(), AvailabilityState.EMPTY);
    }
}
