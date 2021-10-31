package pl.asku.askumagazineservice.magazine.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.asku.askumagazineservice.client.GeocodingClient;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.client.imageservice.MagazinePictureDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.magazine.Geolocation;
import pl.asku.askumagazineservice.service.MagazineService;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class MagazineServiceTestBase {

  @InjectMocks
  protected final MagazineService magazineService;
  protected final MagazineDataProvider magazineDataProvider;
  protected final UserDataProvider userDataProvider;
  @MockBean
  private GeocodingClient geocodingClient;
  @MockBean
  private ImageServiceClient imageServiceClient;

  @Autowired
  public MagazineServiceTestBase(MagazineService magazineService,
                                 MagazineDataProvider magazineDataProvider,
                                 ImageServiceClient imageServiceClient,
                                 UserDataProvider userDataProvider) {
    this.magazineService = magazineService;
    this.magazineDataProvider = magazineDataProvider;
    this.imageServiceClient = imageServiceClient;
    this.userDataProvider = userDataProvider;
  }

  @BeforeEach
  public void setUp() throws LocationNotFoundException, LocationIqRequestFailedException {
    Mockito.when(geocodingClient.getGeolocation(
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString()))
        .thenAnswer(invocationOnMock -> {
              boolean correctLocation = Arrays
                  .stream(invocationOnMock.getArguments())
                  .noneMatch(e -> e != null && e != "");
              if (correctLocation) {
                throw new LocationNotFoundException();
              }
              return new Geolocation(BigDecimal.valueOf(5.0f), BigDecimal.valueOf(5.0f));
            }
        );

    Mockito.when(imageServiceClient.getMagazinePictures(Mockito.anyLong()))
        .thenAnswer(invocationOnMock -> new MagazinePictureDto(invocationOnMock.getArgument(0),
            new ArrayList<>()));
  }

}
