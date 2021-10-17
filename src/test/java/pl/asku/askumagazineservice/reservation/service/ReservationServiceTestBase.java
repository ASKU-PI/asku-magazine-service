package pl.asku.askumagazineservice.reservation.service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import pl.asku.askumagazineservice.client.GeocodingClient;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.magazine.service.MagazineService;
import pl.asku.askumagazineservice.magazine.service.ReservationService;
import pl.asku.askumagazineservice.model.Geolocation;

import java.math.BigDecimal;
import java.util.Arrays;

@SpringBootTest
@ActiveProfiles("test")
public class ReservationServiceTestBase {

    @InjectMocks
    protected final MagazineService magazineService;
    protected final ReservationService reservationService;
    protected final MagazineDataProvider magazineDataProvider;
    @MockBean
    private GeocodingClient geocodingClient;

    @Autowired
    public ReservationServiceTestBase(MagazineService magazineService, MagazineDataProvider magazineDataProvider,
                                      ReservationService reservationService) {
        this.magazineService = magazineService;
        this.magazineDataProvider = magazineDataProvider;
        this.reservationService = reservationService;
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

}
