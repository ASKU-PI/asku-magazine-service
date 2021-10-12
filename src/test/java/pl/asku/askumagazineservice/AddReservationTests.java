package pl.asku.askumagazineservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import pl.asku.askumagazineservice.client.GeocodingClient;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.dto.ReservationDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.model.Geolocation;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.model.Reservation;
import pl.asku.askumagazineservice.service.MagazineService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AddReservationTests {

    @InjectMocks
    private final MagazineService magazineService;
    private final MagazineDto testMagazineDtoTemplate;
    @MockBean
    GeocodingClient geocodingClient;

    @Autowired
    AddReservationTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider) {
        this.magazineService = magazineService;
        this.testMagazineDtoTemplate = magazineDataProvider.validMagazineDto();
    }

    @BeforeEach
    public void setUp() throws LocationNotFoundException, LocationIqRequestFailedException {
        Mockito.when(geocodingClient.getGeolocation(
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString()))
                .thenAnswer(invocationOnMock -> {
                            if (Arrays.stream(invocationOnMock.getArguments()).noneMatch(e -> e != null && e != "")) {
                                throw new LocationNotFoundException();
                            }
                            return new Geolocation(BigDecimal.valueOf(5.0f), BigDecimal.valueOf(5.0f));
                        }
                );
    }

    @Test
    public void returnsCorrectMagazine() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        //when
        BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        ReservationDto reservationDto = new ReservationDto(
                null,
                null,
                null,
                magazine.getStartDate().plusDays(1),
                magazine.getEndDate().minusDays(1),
                area,
                magazine.getId());
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
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        //when
        BigDecimal area = magazineDto.getAreaInMeters();
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        ReservationDto reservationDto = new ReservationDto(
                null,
                null,
                null,
                magazine.getStartDate(),
                magazine.getEndDate(),
                area,
                magazine.getId());
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
    public void failsForMagazineWithNotEnoughSpace() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        //when
        BigDecimal area = magazineDto.getAreaInMeters().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        Optional<Reservation> reservation = magazineService.addReservation(
                new ReservationDto(
                        null,
                        null,
                        null,
                        magazine.getStartDate().plusDays(1),
                        magazine.getEndDate().minusDays(1),
                        area,
                        magazine.getId()),
                username
        );

        //then
        assertTrue(reservation.isEmpty());
    }

    @Test
    public void failsWhenStartDateGreaterThanEndDate() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        //when
        BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        ReservationDto reservationDto = new ReservationDto(
                null,
                null,
                null,
                magazine.getStartDate().plusDays(2),
                magazine.getStartDate().plusDays(1),
                area,
                magazine.getId());
        Optional<Reservation> reservation = magazineService.addReservation(
                reservationDto,
                username
        );

        //then
        assertTrue(reservation.isEmpty());
    }

    @Test
    public void failsWhenDatesIntervalNotCorrect() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        //when
        BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0d));
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        ReservationDto reservationDto = new ReservationDto(
                null,
                null,
                null,
                magazine.getStartDate().minusDays(1),
                magazine.getEndDate().plusDays(1),
                area,
                magazine.getId());
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
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        //when
        BigDecimal area = magazineDto.getAreaInMeters();
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        magazineService.addReservation(
                new ReservationDto(
                        null,
                        null,
                        null,
                        magazine.getStartDate().plusDays(1),
                        magazine.getEndDate().minusDays(1),
                        area,
                        magazine.getId()),
                username
        );
        Optional<Reservation> reservation = magazineService.addReservation(
                new ReservationDto(
                        null,
                        null,
                        null,
                        magazine.getStartDate(),
                        magazine.getEndDate().minusDays(2),
                        area,
                        magazine.getId()),
                username
        );

        //then
        assertTrue(reservation.isEmpty());
    }

    @Test
    public void succeedsWhenDatesIntervalCrossesOtherReservationAndAvailableAreaIsEnough() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        //when
        BigDecimal area = magazineDto.getMinAreaToRent();
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        magazineService.addReservation(
                new ReservationDto(
                        null,
                        null,
                        null,
                        magazine.getStartDate().plusDays(1),
                        magazine.getEndDate().minusDays(1),
                        area,
                        magazine.getId()),
                username
        );
        area = magazineDto.getAreaInMeters().subtract(magazineDto.getMinAreaToRent());
        ReservationDto reservationDto = new ReservationDto(
                null,
                null,
                null,
                magazine.getStartDate(),
                magazine.getEndDate().minusDays(2),
                area,
                magazine.getId());
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
