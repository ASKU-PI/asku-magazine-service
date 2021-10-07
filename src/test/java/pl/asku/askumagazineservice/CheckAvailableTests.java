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
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.model.Geolocation;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.service.MagazineService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class CheckAvailableTests {

    @MockBean
    GeocodingClient geocodingClient;

    @InjectMocks
    private final MagazineService magazineService;

    private final MagazineDto testMagazineDtoTemplate;

    @Autowired
    public CheckAvailableTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider) {
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
    public void returnsTrueWhenNoOtherReservations(){
        //given
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        //when
        BigDecimal area = magazineDto.getMinAreaToRent().add(BigDecimal.valueOf(2.0f));
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        boolean available = magazineService.checkIfMagazineAvailable(
                magazine,
                magazine.getStartDate().plusDays(1),
                magazine.getEndDate().minusDays(1),
                area
        );

        //then
        assertTrue(available);
    }

    //TODO: write tests
}
