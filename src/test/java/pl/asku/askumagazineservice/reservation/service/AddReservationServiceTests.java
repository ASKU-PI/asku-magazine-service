package pl.asku.askumagazineservice.reservation.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.dto.ReservationDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotAvailable;
import pl.asku.askumagazineservice.exception.MagazineNotFound;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.magazine.service.MagazineService;
import pl.asku.askumagazineservice.magazine.service.ReservationService;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.model.Reservation;

import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AddReservationServiceTests extends ReservationServiceTestBase {

    @Autowired
    AddReservationServiceTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider,
                               ReservationService reservationService) {
        super(magazineService, magazineDataProvider, reservationService);
    }

    @Test
    public void returnsCorrectReservation() throws LocationNotFoundException, LocationIqRequestFailedException,
            MagazineNotAvailable, MagazineNotFound {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        //when
        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        Reservation reservation = reservationService.addReservation(
                reservationDto,
                username
        );

        //then
        assertAll(
                () -> assertEquals(reservation.getStartDate(), reservationDto.getStartDate()),
                () -> assertEquals(reservation.getEndDate(), reservationDto.getEndDate()),
                () -> assertEquals(reservation.getAreaInMeters(), reservationDto.getAreaInMeters()),
                () -> assertEquals(reservation.getMagazine().getId(), reservationDto.getMagazineId())
        );
    }

    @Test
    public void reserveFullDateIntervalAndArea() throws LocationNotFoundException, LocationIqRequestFailedException,
            MagazineNotAvailable, MagazineNotFound {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getAreaInMeters();
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate();
        LocalDate endDate = magazine.getEndDate();

        //when
        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        Reservation reservation = reservationService.addReservation(
                reservationDto,
                username
        );

        //then
        assertAll(
                () -> assertEquals(reservation.getStartDate(), reservationDto.getStartDate()),
                () -> assertEquals(reservation.getEndDate(), reservationDto.getEndDate()),
                () -> assertEquals(reservation.getAreaInMeters(), reservationDto.getAreaInMeters()),
                () -> assertEquals(reservation.getMagazine().getId(), reservationDto.getMagazineId())
        );
    }

    @Test
    public void reserveOneDayAndMinimumArea() throws LocationNotFoundException, LocationIqRequestFailedException,
            MagazineNotAvailable, MagazineNotFound {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent();
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate();
        LocalDate endDate = magazine.getStartDate().plusDays(1);

        //when
        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        Reservation reservation = reservationService.addReservation(
                reservationDto,
                username
        );

        //then
        assertAll(
                () -> assertEquals(reservation.getStartDate(), reservationDto.getStartDate()),
                () -> assertEquals(reservation.getEndDate(), reservationDto.getEndDate()),
                () -> assertEquals(reservation.getAreaInMeters(), reservationDto.getAreaInMeters()),
                () -> assertEquals(reservation.getMagazine().getId(), reservationDto.getMagazineId())
        );
    }

    @Test
    public void failsForMagazineTooSmall() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getAreaInMeters().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        //when then
        assertThrows(MagazineNotAvailable.class, () -> reservationService.addReservation(
                reservationDto,
                username
        ));
    }

    @Test
    public void failsWhenStartDateSmallerThanMagazineStartDate() throws LocationNotFoundException,
            LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate().minusDays(1);
        LocalDate endDate = magazine.getEndDate();

        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        //when then
        assertThrows(MagazineNotAvailable.class, () -> reservationService.addReservation(
                reservationDto,
                username
        ));
    }

    @Test
    public void failsWhenEndDateGreaterThanMagazineStartDate() throws LocationNotFoundException,
            LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate();
        LocalDate endDate = magazine.getEndDate().plusDays(1);

        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        //when then
        assertThrows(MagazineNotAvailable.class, () -> reservationService.addReservation(
                reservationDto,
                username
        ));
    }

    @Test
    public void failsWhenStartDateSmallerThanMagazioneStartDateEndDateGreaterThanMagazineStartDate()
            throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate().minusDays(1);
        LocalDate endDate = magazine.getEndDate().plusDays(1);

        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        //when then
        assertThrows(MagazineNotAvailable.class, () -> reservationService.addReservation(
                reservationDto,
                username
        ));
    }

    @Test
    public void failsWhenStartDateGreaterThanMagazineEndDate() throws LocationNotFoundException,
            LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getEndDate().minusDays(1);
        LocalDate endDate = magazine.getStartDate().plusDays(2);

        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        //when then
        assertThrows(ValidationException.class, () -> reservationService.addReservation(
                reservationDto,
                username
        ));
    }

    @Test
    public void failsWhenStartDateEqualsEndDate() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate();
        LocalDate endDate = magazine.getStartDate();

        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        //when then
        assertThrows(ValidationException.class, () -> reservationService.addReservation(
                reservationDto,
                username
        ));
    }

    @Test
    public void failsWhenAreaSmallerThanMinArea() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent().subtract(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        //when then
        assertThrows(MagazineNotAvailable.class, () -> reservationService.addReservation(
                reservationDto,
                username
        ));
    }

    @Test
    public void failsWhenDatesIntervalCrossesOtherReservationAndAvailableAreaIsNotEnough() throws LocationNotFoundException, LocationIqRequestFailedException, MagazineNotAvailable, MagazineNotFound {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        String otherUserIdentifier = magazineDataProvider.otherUserIdentifier();
        BigDecimal areaToRent = magazineDto.getAreaInMeters();
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        reservationService.addReservation(
                ReservationDto.builder()
                        .startDate(startDate.minusDays(1))
                        .endDate(endDate.plusDays(1))
                        .areaInMeters(areaToRent)
                        .magazineId(magazine.getId())
                        .build(),
                otherUserIdentifier
        );

        //when then
        assertThrows(MagazineNotAvailable.class, () -> reservationService.addReservation(
                ReservationDto.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .areaInMeters(areaToRent)
                        .magazineId(magazine.getId())
                        .build(),
                username
        ));
    }

    @Test
    public void succeedsWhenDatesIntervalCrossesOtherReservationAndAvailableAreaIsEnough() throws LocationNotFoundException, LocationIqRequestFailedException, MagazineNotAvailable, MagazineNotFound {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        String otherUserIdentifier = magazineDataProvider.otherUserIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent();
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);
        reservationService.addReservation(
                ReservationDto.builder()
                        .startDate(startDate.minusDays(1))
                        .endDate(endDate.plusDays(1))
                        .areaInMeters(areaToRent)
                        .magazineId(magazine.getId())
                        .build(),
                otherUserIdentifier
        );

        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        //when
        Reservation reservation = reservationService.addReservation(
                reservationDto,
                username
        );

        //then
        assertAll(
                () -> assertEquals(reservation.getStartDate(), reservationDto.getStartDate()),
                () -> assertEquals(reservation.getEndDate(), reservationDto.getEndDate()),
                () -> assertEquals(reservation.getAreaInMeters(), reservationDto.getAreaInMeters()),
                () -> assertEquals(reservation.getMagazine().getId(), reservationDto.getMagazineId())
        );
    }
}