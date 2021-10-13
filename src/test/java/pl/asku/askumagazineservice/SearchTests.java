package pl.asku.askumagazineservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.model.search.LocationFilter;
import pl.asku.askumagazineservice.model.search.MagazineFilters;
import pl.asku.askumagazineservice.service.MagazineService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class SearchTests extends TestBase {

    private final LocationFilter commonLocationFilter = new LocationFilter(
            BigDecimal.valueOf(0.0f),
            BigDecimal.valueOf(10.0f),
            BigDecimal.valueOf(0.0f),
            BigDecimal.valueOf(10.0f)
    );

    @Autowired
    SearchTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider) {
        super(magazineService, magazineDataProvider);
    }

    @Test
    public void searchMagazinesShouldReturnMagazines() {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();

        int magazinesToAdd = 5;
        IntStream.range(0, magazinesToAdd).forEach($ -> {
            try {
                magazineService.addMagazine(magazineDto, username);
            } catch (LocationNotFoundException | LocationIqRequestFailedException e) {
                e.printStackTrace();
            }
        });

        MagazineFilters filters = MagazineFilters.builder()
                .locationFilter(commonLocationFilter)
                .startDateGreaterOrEqual(magazineDto.getStartDate().plusDays(1))
                .endDateLessOrEqual(magazineDto.getEndDate().minusDays(1))
                .minFreeArea(BigDecimal.valueOf(15.0f))
                .maxFreeArea(BigDecimal.valueOf(200.0f))
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
    public void searchMagazinesBooleanFiltersWork() throws LocationNotFoundException, LocationIqRequestFailedException {
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
                .maxFreeArea(BigDecimal.valueOf(200.0f))
                .hasAntiTheftDoors(true)
                .hasMonitoring(true)
                .hasElectricity(true)
                .build();

        magazineService.addMagazine(matchingMagazine, username);
        magazineService.addMagazine(notMatchingMagazine, username);

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
