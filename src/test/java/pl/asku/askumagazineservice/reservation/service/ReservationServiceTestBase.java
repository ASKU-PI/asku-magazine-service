package pl.asku.askumagazineservice.reservation.service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.asku.askumagazineservice.client.GeocodingClient;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.ReservationDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.magazine.Geolocation;
import pl.asku.askumagazineservice.service.MagazineService;
import pl.asku.askumagazineservice.service.ReservationService;

import java.math.BigDecimal;
import java.util.Arrays;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ReservationServiceTestBase {

    @InjectMocks
    protected final MagazineService magazineService;
    protected final ReservationService reservationService;
    protected final MagazineDataProvider magazineDataProvider;
    protected final UserDataProvider userDataProvider;
    protected final ReservationDataProvider reservationDataProvider;
    @MockBean
    private GeocodingClient geocodingClient;

    @Autowired
    public ReservationServiceTestBase(MagazineService magazineService, MagazineDataProvider magazineDataProvider,
                                      ReservationService reservationService, UserDataProvider userDataProvider,
                                      ReservationDataProvider reservationDataProvider) {
        this.magazineService = magazineService;
        this.magazineDataProvider = magazineDataProvider;
        this.reservationService = reservationService;
        this.userDataProvider = userDataProvider;
        this.reservationDataProvider = reservationDataProvider;
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
