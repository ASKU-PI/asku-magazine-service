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
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.magazine.MagazineType;
import pl.asku.askumagazineservice.model.magazine.search.LocationFilter;
import pl.asku.askumagazineservice.model.magazine.search.MagazineFilters;
import pl.asku.askumagazineservice.model.magazine.search.SearchResult;
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
        MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
        User user = userDataProvider.user("test@test.pl", "666666666");

        int magazinesToAdd = 6;
        IntStream.range(0, magazinesToAdd).forEach($ -> {
            try {
                magazineDataProvider.magazine(user, magazineDto);
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
        SearchResult searchResult = magazineService.searchMagazines(
                page,
                filters,
                SortOptions.PRICE_ASC
        );

        //then
        assertEquals(searchResult.getSpaces().size(), magazinesToAdd);
        assertEquals(searchResult.getPages(), 1);
        assertEquals(searchResult.getRecords(), magazinesToAdd);
    }

    @Test
    public void searchMagazinesShouldReturnMeetingRequirements()
            throws LocationNotFoundException, LocationIqRequestFailedException, UserNotFoundException {
        //given
        MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().type(MagazineType.CELL).build();
        User user = userDataProvider.user("test@test.pl", "666666666");

        LocalDate searchStartDate = magazineDto.getStartDate().plusDays(1);
        LocalDate searchEndDate = magazineDto.getEndDate().minusDays(1);
        BigDecimal searchArea = magazineDto.getAreaInMeters().subtract(BigDecimal.valueOf(10.0f));

        int validMagazinesToAdd = 5;
        IntStream.range(0, validMagazinesToAdd).forEach($ -> {
            try {
                magazineDataProvider.magazine(user, magazineDto);
            } catch (LocationNotFoundException | LocationIqRequestFailedException e) {
                e.printStackTrace();
            }
        });

        magazineDataProvider.magazine(user, magazineDto.toBuilder().startDate(searchStartDate.plusDays(1)).build());
        magazineDataProvider.magazine(user, magazineDto.toBuilder().endDate(searchEndDate.minusDays(1)).build());
        magazineDataProvider.magazine(user,
                magazineDto.toBuilder().areaInMeters(searchArea.subtract(BigDecimal.valueOf(10.0f))).build());
        magazineDataProvider.magazine(user,
                magazineDto.toBuilder().minAreaToRent(searchArea.add(BigDecimal.valueOf(10.0f))).build());
        magazineDataProvider.magazine(userDataProvider.user("test2@test.pl", "777777777"), magazineDto);

        MagazineFilters filters = MagazineFilters.builder()
                .locationFilter(commonLocationFilter)
                .startDateGreaterOrEqual(searchStartDate)
                .endDateLessOrEqual(searchEndDate)
                .minFreeArea(searchArea)
                .ownerIdentifier(user.getId())
                .type(MagazineType.CELL)
                .build();

        int page = 1;

        //when
        SearchResult searchResult = magazineService.searchMagazines(
                page,
                filters,
                null
        );

        //then
        searchResult.getSpaces().forEach(magazine -> Assertions.assertAll(
                () -> assertTrue(searchStartDate.compareTo(magazine.getStartDate()) >= 0),
                () -> assertTrue(searchEndDate.compareTo(magazine.getEndDate()) <= 0),
                () -> assertTrue(searchArea.compareTo(magazine.getAreaInMeters()) <= 0),
                () -> assertTrue(searchArea.compareTo(magazine.getMinAreaToRent()) >= 0),
                () -> assertEquals(user.getId(), magazine.getOwnerId()),
                () -> assertEquals(MagazineType.CELL, magazine.getType())
        ));
        assertEquals(searchResult.getPages(), 1);
        assertEquals(searchResult.getRecords(), 5);
    }

    @Test
    public void searchMagazinesBooleanFiltersWork() throws LocationNotFoundException,
            LocationIqRequestFailedException, UserNotFoundException {
        //given
        User user = userDataProvider.user("test@test.pl", "666666666");

        MagazineDto matchingMagazine = magazineDataProvider.magazineDto().toBuilder()
                .antiTheftDoors(true)
                .electricity(false)
                .monitoring(true)
                .build();

        MagazineDto notMatchingMagazine = magazineDataProvider.magazineDto().toBuilder()
                .antiTheftDoors(false)
                .electricity(true)
                .monitoring(false)
                .build();

        int page = 1;

        MagazineFilters filters = MagazineFilters.builder()
                .locationFilter(commonLocationFilter)
                .startDateGreaterOrEqual(matchingMagazine.getStartDate().plusDays(1))
                .endDateLessOrEqual(matchingMagazine.getEndDate().minusDays(1))
                .minFreeArea(BigDecimal.valueOf(15.0f))
                .hasAntiTheftDoors(true)
                .hasMonitoring(true)
                .hasElectricity(false)
                .build();

        magazineDataProvider.magazine(user, matchingMagazine);
        magazineDataProvider.magazine(user, notMatchingMagazine);

        //when
        SearchResult searchResult = magazineService.searchMagazines(
                page,
                filters,
                null
        );

        //then
        searchResult.getSpaces().forEach(magazine -> Assertions.assertAll(
                () -> assertEquals(true, magazine.getAntiTheftDoors()),
                () -> assertEquals(true, magazine.getMonitoring()),
                () -> assertEquals(false, magazine.getElectricity())
        ));
        assertEquals(searchResult.getPages(), 1);
        assertEquals(searchResult.getRecords(), 1);
    }

    @Test
    public void searchMagazinesShouldLimitResultsPerPage() throws UserNotFoundException {
        //given
        MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
        User user = userDataProvider.user("test@test.pl", "666666666");

        int magazinesToAdd = 100;
        IntStream.range(0, magazinesToAdd).forEach($ -> {
            try {
                magazineDataProvider.magazine(user, magazineDto);
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
        SearchResult searchResult = magazineService.searchMagazines(
                page,
                filters,
                null
        );

        //then
        assertEquals(20, searchResult.getSpaces().size());
        assertEquals(searchResult.getPages(), 5);
        assertEquals(searchResult.getRecords(), 100);
    }

    @Test
    public void shouldFilterNotAvailableMagazines() throws UserNotFoundException {
        //given
        MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
        User user = userDataProvider.user("test@test.pl", "666666666");

        int availableMagazines = 5;
        IntStream.range(0, availableMagazines).forEach($ -> {
            try {
                magazineDataProvider.magazine(user, magazineDto);
            } catch (LocationNotFoundException | LocationIqRequestFailedException e) {
                e.printStackTrace();
            }
        });

        int unavailableMagazines = 5;
        IntStream.range(0, unavailableMagazines).forEach($ -> {
            try {
                Magazine magazine = magazineDataProvider.magazine(user, magazineDto);

                reservationService.addReservation(
                        ReservationDto.builder()
                                .startDate(magazineDto.getStartDate())
                                .endDate(magazineDto.getEndDate())
                                .areaInMeters(magazineDto.getAreaInMeters())
                                .magazineId(magazine.getId())
                                .build(),
                        user.getId()
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
        SearchResult searchResult = magazineService.searchMagazines(
                page,
                filters,
                null
        );

        //then
        assertEquals(searchResult.getSpaces().size(), availableMagazines);
        assertEquals(searchResult.getPages(), 1);
        assertEquals(searchResult.getRecords(), 5);
    }

    @Test
    public void shouldFilterCurrentlyReservedBy() throws UserNotFoundException {
        //given
        MagazineDto magazineDto = magazineDataProvider.magazineDto().toBuilder().build();
        User user = userDataProvider.user("test@test.pl", "666666666");

        int availableMagazines = 5;
        IntStream.range(0, availableMagazines).forEach($ -> {
            try {
                magazineDataProvider.magazine(user, magazineDto);
            } catch (LocationNotFoundException | LocationIqRequestFailedException e) {
                e.printStackTrace();
            }
        });

        int unavailableMagazines = 5;
        IntStream.range(0, unavailableMagazines).forEach($ -> {
            try {
                Magazine magazine = magazineDataProvider.magazine(user, magazineDto);

                reservationService.addReservation(
                        ReservationDto.builder()
                                .startDate(magazineDto.getStartDate())
                                .endDate(magazineDto.getEndDate())
                                .areaInMeters(magazineDto.getAreaInMeters())
                                .magazineId(magazine.getId())
                                .build(),
                        user.getId()
                );
            } catch (LocationNotFoundException | LocationIqRequestFailedException | MagazineNotAvailableException | MagazineNotFoundException e) {
                e.printStackTrace();
            }
        });

        MagazineFilters filters = MagazineFilters.builder()
                .currentlyReservedBy(user.getId())
                .build();

        int page = 1;

        //when
        SearchResult searchResult = magazineService.searchMagazines(
                page,
                filters,
                null
        );

        //then
        assertEquals(searchResult.getSpaces().size(), availableMagazines);
        assertEquals(searchResult.getPages(), 1);
        assertEquals(searchResult.getRecords(), 5);
    }

}
