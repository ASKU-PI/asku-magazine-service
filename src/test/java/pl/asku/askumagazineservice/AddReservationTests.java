package pl.asku.askumagazineservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.dto.ReservationDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.model.Reservation;
import pl.asku.askumagazineservice.service.MagazineService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AddReservationTests extends TestBase {

    @Autowired
    AddReservationTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider) {
        super(magazineService, magazineDataProvider);
    }

    @Test
    public void returnsCorrectReservation() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        //when
        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        Optional<Reservation> reservation = magazineService.addReservation(
                reservationDto,
                username
        );

        //then
        assertAll(
                () -> assertTrue(reservation.isPresent()),
                () -> assertEquals(reservation.get().getStartDate(), reservationDto.getStartDate()),
                () -> assertEquals(reservation.get().getEndDate(), reservationDto.getEndDate()),
                () -> assertEquals(reservation.get().getAreaInMeters(), reservationDto.getAreaInMeters()),
                () -> assertEquals(reservation.get().getMagazine().getId(), reservationDto.getMagazineId())
        );
    }

    @Test
    public void reserveFullDateIntervalAndArea() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getAreaInMeters();
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        LocalDate startDate = magazine.getStartDate();
        LocalDate endDate = magazine.getEndDate();

        //when
        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        Optional<Reservation> reservation = magazineService.addReservation(
                reservationDto,
                username
        );

        //then
        assertAll(
                () -> assertTrue(reservation.isPresent()),
                () -> assertEquals(reservation.get().getStartDate(), reservationDto.getStartDate()),
                () -> assertEquals(reservation.get().getEndDate(), reservationDto.getEndDate()),
                () -> assertEquals(reservation.get().getAreaInMeters(), reservationDto.getAreaInMeters()),
                () -> assertEquals(reservation.get().getMagazine().getId(), reservationDto.getMagazineId())
        );
    }

    @Test
    public void reserveOneDayAndMinimumArea() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent();
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        LocalDate startDate = magazine.getStartDate();
        LocalDate endDate = magazine.getStartDate().plusDays(1);

        //when
        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        Optional<Reservation> reservation = magazineService.addReservation(
                reservationDto,
                username
        );

        //then
        assertAll(
                () -> assertTrue(reservation.isPresent()),
                () -> assertEquals(reservation.get().getStartDate(), reservationDto.getStartDate()),
                () -> assertEquals(reservation.get().getEndDate(), reservationDto.getEndDate()),
                () -> assertEquals(reservation.get().getAreaInMeters(), reservationDto.getAreaInMeters()),
                () -> assertEquals(reservation.get().getMagazine().getId(), reservationDto.getMagazineId())
        );
    }

    @Test
    public void failsForMagazineTooSmall() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getAreaInMeters().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        //when
        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        Optional<Reservation> reservation = magazineService.addReservation(
                reservationDto,
                username
        );

        //then
        assertTrue(reservation.isEmpty());
    }

    @Test
    public void failsWhenStartDateSmallerThanMagazineStartDate() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        LocalDate startDate = magazine.getStartDate().minusDays(1);
        LocalDate endDate = magazine.getEndDate();

        //when
        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        Optional<Reservation> reservation = magazineService.addReservation(
                reservationDto,
                username
        );

        //then
        assertTrue(reservation.isEmpty());
    }

    @Test
    public void failsWhenEndDateGreaterThanMagazineStartDate() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        LocalDate startDate = magazine.getStartDate();
        LocalDate endDate = magazine.getEndDate().plusDays(1);

        //when
        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        Optional<Reservation> reservation = magazineService.addReservation(
                reservationDto,
                username
        );

        //then
        assertTrue(reservation.isEmpty());
    }

    @Test
    public void failsWhenStartDateSmallerThanMagazioneStartDateEndDateGreaterThanMagazineStartDate()
            throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        LocalDate startDate = magazine.getStartDate().minusDays(1);
        LocalDate endDate = magazine.getEndDate().plusDays(1);

        //when
        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        Optional<Reservation> reservation = magazineService.addReservation(
                reservationDto,
                username
        );

        //then
        assertTrue(reservation.isEmpty());
    }

    @Test
    public void failsWhenStartDateGreaterThanMagazineEndDate() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        LocalDate startDate = magazine.getEndDate().minusDays(1);
        LocalDate endDate = magazine.getStartDate().plusDays(2);

        //when
        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        Optional<Reservation> reservation = magazineService.addReservation(
                reservationDto,
                username
        );

        //then
        assertTrue(reservation.isEmpty());
    }

    @Test
    public void failsWhenStartDateEqualsEndDate() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        LocalDate startDate = magazine.getStartDate();
        LocalDate endDate = magazine.getStartDate();

        //when
        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        Optional<Reservation> reservation = magazineService.addReservation(
                reservationDto,
                username
        );

        //then
        assertTrue(reservation.isEmpty());
    }

    @Test
    public void failsWhenAreaSmallerThanMinArea() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent().subtract(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        //when
        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        Optional<Reservation> reservation = magazineService.addReservation(
                reservationDto,
                username
        );

        //then
        assertTrue(reservation.isEmpty());
    }

    @Test
    public void failsWhenDatesIntervalCrossesOtherReservationAndAvailableAreaIsNotEnough() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        String otherUserIdentifier = magazineDataProvider.otherUserIdentifier();
        BigDecimal areaToRent = magazineDto.getAreaInMeters();
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        magazineService.addReservation(
                ReservationDto.builder()
                        .startDate(startDate.minusDays(1))
                        .endDate(endDate.plusDays(1))
                        .areaInMeters(areaToRent)
                        .magazineId(magazine.getId())
                        .build(),
                otherUserIdentifier
        );

        //when
        Optional<Reservation> reservation = magazineService.addReservation(
                ReservationDto.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .areaInMeters(areaToRent)
                        .magazineId(magazine.getId())
                        .build(),
                username
        );

        //then
        assertTrue(reservation.isEmpty());
    }

    @Test
    public void succeedsWhenDatesIntervalCrossesOtherReservationAndAvailableAreaIsEnough() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        String otherUserIdentifier = magazineDataProvider.otherUserIdentifier();
        BigDecimal areaToRent = magazineDto.getMinAreaToRent();
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);
        magazineService.addReservation(
                ReservationDto.builder()
                        .startDate(startDate.minusDays(1))
                        .endDate(endDate.plusDays(1))
                        .areaInMeters(areaToRent)
                        .magazineId(magazine.getId())
                        .build(),
                otherUserIdentifier
        );

        //when
        ReservationDto reservationDto = ReservationDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .areaInMeters(areaToRent)
                .magazineId(magazine.getId())
                .build();

        Optional<Reservation> reservation = magazineService.addReservation(
                reservationDto,
                username
        );

        //then
        assertAll(
                () -> assertTrue(reservation.isPresent()),
                () -> assertEquals(reservation.get().getStartDate(), reservationDto.getStartDate()),
                () -> assertEquals(reservation.get().getEndDate(), reservationDto.getEndDate()),
                () -> assertEquals(reservation.get().getAreaInMeters(), reservationDto.getAreaInMeters()),
                () -> assertEquals(reservation.get().getMagazine().getId(), reservationDto.getMagazineId())
        );
    }
}
