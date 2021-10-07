package pl.asku.askumagazineservice;

import org.junit.jupiter.api.Assertions;
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
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.model.Geolocation;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.service.MagazineService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class SearchTests {

    @InjectMocks
    private final MagazineService magazineService;

    private final MagazineDto testMagazineDtoTemplate;

    @MockBean
    private GeocodingClient geocodingClient;

    @Autowired
    SearchTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider) {
        this.magazineService = magazineService;
        this.testMagazineDtoTemplate = magazineDataProvider.validMagazineDto();
    }

    @BeforeEach
    public void setUp() {
        Mockito.when(geocodingClient.getGeolocation(
                        testMagazineDtoTemplate.getCountry(),
                        testMagazineDtoTemplate.getCity(),
                        testMagazineDtoTemplate.getStreet(),
                        testMagazineDtoTemplate.getBuilding()))
                .thenAnswer(invocationOnMock -> {
                            if (Arrays.stream(invocationOnMock.getArguments()).noneMatch(e -> e != null && e != "")) {
                                return Optional.empty();
                            }
                            return Optional.of(new Geolocation(BigDecimal.valueOf(5.0f), BigDecimal.valueOf(5.0f)));
                        }
                );
    }

    @Test
    public void searchMagazinesShouldReturnMagazines() {
        //given
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        int magazinesToAdd = 5;
        int page = 0;
        LocalDate startDate = magazineDto.getStartDate().plusDays(1);
        LocalDate endDate = magazineDto.getEndDate().minusDays(1);
        BigDecimal minLongitude = BigDecimal.valueOf(0.0f);
        BigDecimal maxLongitude = BigDecimal.valueOf(10.0f);
        BigDecimal minLatitude = BigDecimal.valueOf(0.0f);
        BigDecimal maxLatitude = BigDecimal.valueOf(10.0f);
        BigDecimal minArea = BigDecimal.valueOf(15.0f);
        BigDecimal maxArea = BigDecimal.valueOf(200.0f);

        //when
        IntStream.range(0, magazinesToAdd).forEach($ -> magazineService.addMagazine(magazineDto, username));
        List<Magazine> searchResult = magazineService.searchMagazines(
                page,
                minLongitude,
                maxLongitude,
                minLatitude,
                maxLatitude,
                null,
                null,
                startDate,
                endDate,
                minArea,
                maxArea,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        //then
        Assertions.assertTrue(searchResult.size() >= magazinesToAdd);

    }

    @Test
    public void searchMagazinesBooleanFiltersWork() {
        //given
        String username = "test";

        Boolean antiTheftDoors = true;
        Boolean electricity = false;
        Boolean monitoring = true;

        MagazineDto matchingMagazine = testMagazineDtoTemplate.toBuilder().build();
        matchingMagazine.setAntiTheftDoors(antiTheftDoors);
        matchingMagazine.setElectricity(electricity);
        matchingMagazine.setMonitoring(monitoring);

        MagazineDto notMatchingMagazine = testMagazineDtoTemplate.toBuilder().build();
        notMatchingMagazine.setAntiTheftDoors(!antiTheftDoors);
        notMatchingMagazine.setElectricity(!electricity);
        notMatchingMagazine.setMonitoring(!monitoring);

        int page = 0;
        LocalDate startDate = matchingMagazine.getStartDate().plusDays(1);
        LocalDate endDate = matchingMagazine.getEndDate().minusDays(1);
        BigDecimal minLongitude = BigDecimal.valueOf(0.0f);
        BigDecimal maxLongitude = BigDecimal.valueOf(10.0f);
        BigDecimal minLatitude = BigDecimal.valueOf(0.0f);
        BigDecimal maxLatitude = BigDecimal.valueOf(10.0f);
        BigDecimal minArea = BigDecimal.valueOf(15.0f);
        BigDecimal maxArea = BigDecimal.valueOf(200.0f);

        //when
        magazineService.addMagazine(matchingMagazine, username);
        magazineService.addMagazine(notMatchingMagazine, username);
        List<Magazine> searchResult = magazineService.searchMagazines(
                page,
                minLongitude,
                maxLongitude,
                minLatitude,
                maxLatitude,
                null,
                null,
                startDate,
                endDate,
                minArea,
                maxArea,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(antiTheftDoors),
                Optional.of(monitoring),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(electricity),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        //then
        searchResult.forEach(magazine -> Assertions.assertAll(
                () -> assertEquals(antiTheftDoors, magazine.getAntiTheftDoors()),
                () -> assertEquals(monitoring, magazine.getMonitoring()),
                () -> assertEquals(electricity, magazine.getElectricity())
        ));
    }

}
