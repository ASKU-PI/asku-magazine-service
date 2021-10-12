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
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
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
public class GetTests {
    @MockBean
    GeocodingClient geocodingClient;

    @InjectMocks
    private final MagazineService magazineService;

    private final MagazineDto testMagazineDtoTemplate;

    @Autowired
    GetTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider) {
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
    public void getMagazineDetailsShouldReturnCorrectMagazine() throws LocationNotFoundException, LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        //when
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        Optional<Magazine> magazineDetails = magazineService.getMagazineDetails(magazine.getId());

        //then
        assertTrue(magazineDetails.isPresent());;
        Assertions.assertEquals(magazine.getId(), magazineDetails.get().getId());
    }
}
