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

import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CheckAvailableMagazineServiceTests extends ReservationServiceTestBase {

    @Autowired
    public CheckAvailableMagazineServiceTests(MagazineService magazineService,
                                              MagazineDataProvider magazineDataProvider,
                                              ReservationService reservationService) {
        super(magazineService, magazineDataProvider, reservationService);
    }

    @Test
    public void returnsTrueWhenNoOtherReservations() throws LocationNotFoundException,
            LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0f));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        //when
        boolean available = reservationService.checkIfMagazineAvailable(
                magazine,
                startDate,
                endDate,
                area
        );

        //then
        assertTrue(available);
    }

    @Test
    public void returnsTrueWhenFullDateIntervalAndArea() throws LocationNotFoundException,
            LocationIqRequestFailedException {
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal area = magazineDto.getAreaInMeters();
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        //when
        boolean available = reservationService.checkIfMagazineAvailable(
                magazine,
                startDate,
                endDate,
                area
        );

        //then
        assertTrue(available);
    }

    @Test
    public void returnsTrueWhenOneDayAndMinimumArea() throws LocationNotFoundException,
            LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal area = magazineDto.getMinAreaToRent();
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate();
        LocalDate endDate = magazine.getStartDate().plusDays(1);

        //when
        boolean available = reservationService.checkIfMagazineAvailable(
                magazine,
                startDate,
                endDate,
                area
        );

        //then
        assertTrue(available);
    }

    @Test
    public void returnsFalseMagazineTooSmall() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal area = magazineDto.getAreaInMeters().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        //when
        boolean available = reservationService.checkIfMagazineAvailable(
                magazine,
                startDate,
                endDate,
                area
        );

        //then
        assertFalse(available);
    }

    @Test
    public void returnsFalseWhenStartDateSmallerThanMagazineStartDate() throws LocationNotFoundException,
            LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate().minusDays(1);
        LocalDate endDate = magazine.getEndDate();

        //when
        boolean available = reservationService.checkIfMagazineAvailable(
                magazine,
                startDate,
                endDate,
                area
        );

        //then
        assertFalse(available);
    }

    @Test
    public void returnsFalseWhenEndDateGreaterThanMagazineStartDate() throws LocationNotFoundException,
            LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate();
        LocalDate endDate = magazine.getEndDate().plusDays(1);

        //when
        boolean available = reservationService.checkIfMagazineAvailable(
                magazine,
                startDate,
                endDate,
                area
        );

        //then
        assertFalse(available);
    }

    @Test
    public void returnsFalseWhenStartDateSmallerThanMagazioneStartDateEndDateGreaterThanMagazineStartDate()
            throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate().minusDays(1);
        LocalDate endDate = magazine.getEndDate().plusDays(1);

        //when
        boolean available = reservationService.checkIfMagazineAvailable(
                magazine,
                startDate,
                endDate,
                area
        );

        //then
        assertFalse(available);
    }

    @Test
    public void returnsFalseWhenStartDateGreaterThanMagazineEndDate() throws LocationNotFoundException,
            LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getEndDate().minusDays(1);
        LocalDate endDate = magazine.getStartDate().plusDays(1);

        //when then
        assertThrows(ValidationException.class, () -> reservationService.checkIfMagazineAvailable(
                magazine,
                startDate,
                endDate,
                area
        ));
    }

    @Test
    public void returnsFalseWhenStartDateEqualsEndDate() throws LocationNotFoundException,
            LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate();
        LocalDate endDate = magazine.getStartDate();

        //when then
        assertThrows(ValidationException.class, () -> reservationService.checkIfMagazineAvailable(
                magazine,
                startDate,
                endDate,
                area
        ));
    }

    @Test
    public void returnsFalseWhenAreaSmallerThanMinArea() throws LocationNotFoundException,
            LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        BigDecimal area = magazineDto.getMinAreaToRent().subtract(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);
        LocalDate startDate = magazine.getStartDate().plusDays(1);
        LocalDate endDate = magazine.getEndDate().minusDays(1);

        //when
        boolean available = reservationService.checkIfMagazineAvailable(
                magazine,
                startDate,
                endDate,
                area
        );

        //then
        assertFalse(available);
    }

    @Test
    public void returnsFalseWhenDatesIntervalCrossesOtherReservationAndAvailableAreaIsNotEnough() throws LocationNotFoundException, LocationIqRequestFailedException, MagazineNotAvailable, MagazineNotFound {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        String otherUserIdentifier = magazineDataProvider.otherUserIdentifier();
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
        boolean available = reservationService.checkIfMagazineAvailable(
                magazine,
                startDate,
                endDate,
                area
        );

        //then
        assertFalse(available);
    }

    @Test
    public void returnsTrueWhenDatesIntervalCrossesOtherReservationAndAvailableAreaIsEnough() throws LocationNotFoundException, LocationIqRequestFailedException, MagazineNotAvailable, MagazineNotFound {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        String otherUserIdentifier = magazineDataProvider.otherUserIdentifier();
        BigDecimal area = magazineDto.getMinAreaToRent();
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
        boolean available = reservationService.checkIfMagazineAvailable(
                magazine,
                startDate,
                endDate,
                area
        );

        //then
        assertTrue(available);
    }
}