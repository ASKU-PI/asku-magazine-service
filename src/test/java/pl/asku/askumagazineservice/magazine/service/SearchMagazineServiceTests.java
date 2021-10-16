package pl.asku.askumagazineservice.magazine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.model.search.LocationFilter;
import pl.asku.askumagazineservice.model.search.MagazineFilters;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class SearchMagazineServiceTests extends MagazineServiceTestBase {

    private final LocationFilter commonLocationFilter = new LocationFilter(
            BigDecimal.valueOf(0.0f),
            BigDecimal.valueOf(10.0f),
            BigDecimal.valueOf(0.0f),
            BigDecimal.valueOf(10.0f)
    );

    @Autowired
    SearchMagazineServiceTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider,
                               ImageServiceClient imageServiceClient) {
        super(magazineService, magazineDataProvider, imageServiceClient);
    }

    @Test
    public void searchMagazinesShouldReturnMagazines() {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();

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
                filters
        );

        //then
        Assertions.assertTrue(searchResult.size() >= magazinesToAdd);
    }

    @Test
    public void searchMagazinesShouldReturnMeetingRequirements()
            throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();

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
                magazineDataProvider.otherUserIdentifier(),
                null
        );

        MagazineFilters filters = MagazineFilters.builder()
                .locationFilter(commonLocationFilter)
                .startDateGreaterOrEqual(searchStartDate)
                .endDateLessOrEqual(searchEndDate)
                .minFreeArea(searchArea)
                .ownerIdentifier(username)
                .build();

        int page = 1;

        //when
        List<Magazine> searchResult = magazineService.searchMagazines(
                page,
                filters
        );

        //then
        searchResult.forEach(magazine -> Assertions.assertAll(
                () -> assertTrue(searchStartDate.compareTo(magazine.getStartDate()) >= 0),
                () -> assertTrue(searchEndDate.compareTo(magazine.getEndDate()) <= 0),
                () -> assertTrue(searchArea.compareTo(magazine.getAreaInMeters()) <= 0),
                () -> assertTrue(searchArea.compareTo(magazine.getMinAreaToRent()) >= 0),
                () -> assertEquals(username, magazine.getOwner())
        ));
    }

    @Test
    public void searchMagazinesBooleanFiltersWork() throws LocationNotFoundException,
            LocationIqRequestFailedException {
        //given
        String username = magazineDataProvider.userIdentifier();

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
                filters
        );

        //then
        searchResult.forEach(magazine -> Assertions.assertAll(
                () -> assertEquals(antiTheftDoors, magazine.getAntiTheftDoors()),
                () -> assertEquals(monitoring, magazine.getMonitoring()),
                () -> assertEquals(electricity, magazine.getElectricity())
        ));
    }

}
