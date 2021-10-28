package pl.asku.askumagazineservice.magazine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.dto.reservation.ReservationDto;
import pl.asku.askumagazineservice.exception.*;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.magazine.MagazineType;
import pl.asku.askumagazineservice.model.magazine.search.LocationFilter;
import pl.asku.askumagazineservice.model.magazine.search.MagazineFilters;
import pl.asku.askumagazineservice.model.magazine.search.SortOptions;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchMagazineServiceTests extends MagazineServiceTestBase {

    private final LocationFilter commonLocationFilter = new LocationFilter(
            BigDecimal.valueOf(0.0f),
            BigDecimal.valueOf(10.0f),
            BigDecimal.valueOf(0.0f),
            BigDecimal.valueOf(10.0f)
    );

    private final ReservationService reservationService;

    @Autowired
    SearchMagazineServiceTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider,
                               ImageServiceClient imageServiceClient, ReservationService reservationService,
                               UserDataProvider userDataProvider) {
        super(magazineService, magazineDataProvider, imageServiceClient, userDataProvider);
        this.reservationService = reservationService;
    }

    @Test
    public void searchMagazinesShouldReturnMagazines() throws UserNotFoundException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = userDataProvider.getUser("test@test.pl").getId();

        int magazinesToAdd = 5;
        IntStream.range(0, magazinesToAdd).forEach($ -> {
            try {
                magazineService.addMagazine(magazineDto, username, null);
            } catch (LocationNotFoundException | LocationIqRequestFailedException e) {
                e.printStackTrace();
            }
        });

        MagazineFilters filters = MagazineFilters.builder()
                .locationFilter(commonLocationFilter)
                .startDateGreaterOrEqual(magazineDto.getStartDate().plusDays(1))
                .endDateLessOrEqual(magazineDto.getEndDate().minusDays(1))
                .minFreeArea(BigDecimal.valueOf(15.0f))
                .build();

        int page = 1;

        //when
        List<Magazine> searchResult = magazineService.searchMagazines(
                page,
                filters,
                SortOptions.PRICE_ASC
        );

        //then
        Assertions.assertTrue(searchResult.size() == magazinesToAdd);
    }

    @Test
    public void searchMagazinesShouldReturnMeetingRequirements()
            throws LocationNotFoundException, LocationIqRequestFailedException, UserNotFoundException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().type(MagazineType.CELL).build();
        String username = userDataProvider.getUser("test@test.pl").getId();

        LocalDate searchStartDate = magazineDto.getStartDate().plusDays(1);
        LocalDate searchEndDate = magazineDto.getEndDate().minusDays(1);
        BigDecimal searchArea = magazineDto.getAreaInMeters().subtract(BigDecimal.valueOf(10.0f));

        int validMagazinesToAdd = 5;
        IntStream.range(0, validMagazinesToAdd).forEach($ -> {
            try {
                magazineService.addMagazine(magazineDto, username, null);
            } catch (LocationNotFoundException | LocationIqRequestFailedException e) {
                e.printStackTrace();
            }
        });

        magazineService.addMagazine(
                magazineDto.toBuilder().startDate(searchStartDate.plusDays(1)).build(),
                username,
                null
        );

        magazineService.addMagazine(
                magazineDto.toBuilder().endDate(searchEndDate.minusDays(1)).build(),
                username,
                null
        );

        magazineService.addMagazine(
                magazineDto.toBuilder().areaInMeters(searchArea.subtract(BigDecimal.valueOf(10.0f))).build(),
                username,
                null
        );

        magazineService.addMagazine(
                magazineDto.toBuilder().minAreaToRent(searchArea.add(BigDecimal.valueOf(10.0f))).build(),
                username,
                null
        );

        magazineService.addMagazine(
                magazineDto,
                userDataProvider.getUser("test2@test.pl").getId(),
                null
        );

        MagazineFilters filters = MagazineFilters.builder()
                .locationFilter(commonLocationFilter)
                .startDateGreaterOrEqual(searchStartDate)
                .endDateLessOrEqual(searchEndDate)
                .minFreeArea(searchArea)
                .ownerIdentifier(username)
                .type(MagazineType.CELL)
                .build();

        int page = 1;

        //when
        List<Magazine> searchResult = magazineService.searchMagazines(
                page,
                filters,
                null
        );

        //then
        searchResult.forEach(magazine -> Assertions.assertAll(
                () -> assertTrue(searchStartDate.compareTo(magazine.getStartDate()) >= 0),
                () -> assertTrue(searchEndDate.compareTo(magazine.getEndDate()) <= 0),
                () -> assertTrue(searchArea.compareTo(magazine.getAreaInMeters()) <= 0),
                () -> assertTrue(searchArea.compareTo(magazine.getMinAreaToRent()) >= 0),
                () -> assertEquals(username, magazine.getOwnerId()),
                () -> assertEquals(MagazineType.CELL, magazine.getType())
        ));
    }

    @Test
    public void searchMagazinesBooleanFiltersWork() throws LocationNotFoundException,
            LocationIqRequestFailedException, UserNotFoundException {
        //given
        String username = userDataProvider.getUser("test@test.pl").getId();

        Boolean antiTheftDoors = true;
        Boolean electricity = false;
        Boolean monitoring = true;

        MagazineDto matchingMagazine = magazineDataProvider.validMagazineDto().toBuilder().build();
        matchingMagazine.setAntiTheftDoors(antiTheftDoors);
        matchingMagazine.setElectricity(electricity);
        matchingMagazine.setMonitoring(monitoring);

        MagazineDto notMatchingMagazine = magazineDataProvider.validMagazineDto().toBuilder().build();
        notMatchingMagazine.setAntiTheftDoors(!antiTheftDoors);
        notMatchingMagazine.setElectricity(!electricity);
        notMatchingMagazine.setMonitoring(!monitoring);

        int page = 1;

        MagazineFilters filters = MagazineFilters.builder()
                .locationFilter(commonLocationFilter)
                .startDateGreaterOrEqual(matchingMagazine.getStartDate().plusDays(1))
                .endDateLessOrEqual(matchingMagazine.getEndDate().minusDays(1))
                .minFreeArea(BigDecimal.valueOf(15.0f))
                .hasAntiTheftDoors(true)
                .hasMonitoring(true)
                .hasElectricity(true)
                .build();

        magazineService.addMagazine(matchingMagazine, username, null);
        magazineService.addMagazine(notMatchingMagazine, username, null);

        //when
        List<Magazine> searchResult = magazineService.searchMagazines(
                page,
                filters,
                null
        );

        //then
        searchResult.forEach(magazine -> Assertions.assertAll(
                () -> assertEquals(antiTheftDoors, magazine.getAntiTheftDoors()),
                () -> assertEquals(monitoring, magazine.getMonitoring()),
                () -> assertEquals(electricity, magazine.getElectricity())
        ));
    }

    @Test
    public void searchMagazinesShouldLimitResultsPerPage() throws UserNotFoundException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = userDataProvider.getUser("test@test.pl").getId();

        int magazinesToAdd = 100;
        IntStream.range(0, magazinesToAdd).forEach($ -> {
            try {
                magazineService.addMagazine(magazineDto, username, null);
            } catch (LocationNotFoundException | LocationIqRequestFailedException e) {
                e.printStackTrace();
            }
        });

        MagazineFilters filters = MagazineFilters.builder()
                .locationFilter(commonLocationFilter)
                .startDateGreaterOrEqual(magazineDto.getStartDate().plusDays(1))
                .endDateLessOrEqual(magazineDto.getEndDate().minusDays(1))
                .minFreeArea(BigDecimal.valueOf(15.0f))
                .build();

        int page = 1;

        //when
        List<Magazine> searchResult = magazineService.searchMagazines(
                page,
                filters,
                null
        );

        //then
        assertEquals(20, searchResult.size());
    }

    @Test
    public void shouldFilterNotAvailableMagazines() throws UserNotFoundException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = userDataProvider.getUser("test@test.pl").getId();

        int availableMagazines = 5;
        IntStream.range(0, availableMagazines).forEach($ -> {
            try {
                magazineService.addMagazine(magazineDto, username, null);
            } catch (LocationNotFoundException | LocationIqRequestFailedException e) {
                e.printStackTrace();
            }
        });

        int unavailableMagazines = 5;
        IntStream.range(0, unavailableMagazines).forEach($ -> {
            try {
                Magazine magazine = magazineService.addMagazine(magazineDto, username, null);

                reservationService.addReservation(
                        ReservationDto.builder()
                                .startDate(magazineDto.getStartDate())
                                .endDate(magazineDto.getEndDate())
                                .areaInMeters(magazineDto.getAreaInMeters())
                                .magazineId(magazine.getId())
                                .build(),
                        username
                );
            } catch (LocationNotFoundException | LocationIqRequestFailedException | MagazineNotAvailableException | MagazineNotFoundException e) {
                e.printStackTrace();
            }
        });

        MagazineFilters filters = MagazineFilters.builder()
                .locationFilter(commonLocationFilter)
                .startDateGreaterOrEqual(magazineDto.getStartDate().plusDays(1))
                .endDateLessOrEqual(magazineDto.getEndDate().minusDays(1))
                .availableOnly(true)
                .build();

        int page = 1;

        //when
        List<Magazine> searchResult = magazineService.searchMagazines(
                page,
                filters,
                null
        );

        //then
        Assertions.assertTrue(searchResult.size() == availableMagazines);
    }

    @Test
    public void shouldFilterCurrentlyReservedBy() throws UserNotFoundException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = userDataProvider.getUser("test@test.pl").getId();

        int availableMagazines = 5;
        IntStream.range(0, availableMagazines).forEach($ -> {
            try {
                magazineService.addMagazine(magazineDto, username, null);
            } catch (LocationNotFoundException | LocationIqRequestFailedException e) {
                e.printStackTrace();
            }
        });

        int unavailableMagazines = 5;
        IntStream.range(0, unavailableMagazines).forEach($ -> {
            try {
                Magazine magazine = magazineService.addMagazine(magazineDto, username, null);

                reservationService.addReservation(
                        ReservationDto.builder()
                                .startDate(magazineDto.getStartDate())
                                .endDate(magazineDto.getEndDate())
                                .areaInMeters(magazineDto.getAreaInMeters())
                                .magazineId(magazine.getId())
                                .build(),
                        username
                );
            } catch (LocationNotFoundException | LocationIqRequestFailedException | MagazineNotAvailableException | MagazineNotFoundException e) {
                e.printStackTrace();
            }
        });

        MagazineFilters filters = MagazineFilters.builder()
                .currentlyReservedBy(username)
                .build();

        int page = 1;

        //when
        List<Magazine> searchResult = magazineService.searchMagazines(
                page,
                filters,
                null
        );

        //then
        Assertions.assertTrue(searchResult.size() == availableMagazines);
    }

}
