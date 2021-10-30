package pl.asku.askumagazineservice.magazine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.service.MagazineService;

public class GetMagazineServiceTests extends MagazineServiceTestBase {

    @Autowired
    GetMagazineServiceTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider,
                            ImageServiceClient imageServiceClient, UserDataProvider userDataProvider) {
        super(magazineService, magazineDataProvider, imageServiceClient, userDataProvider);
    }

    @Test
    public void getMagazineDetailsShouldReturnCorrectMagazine() throws LocationNotFoundException,
            LocationIqRequestFailedException, MagazineNotFoundException {
        //given
        User user = userDataProvider.user("test@test.pl", "666666666");
        Magazine magazine = magazineDataProvider.magazine(user);

        //when
        Magazine magazineDetails = magazineService.getMagazineDetails(magazine.getId());

        //then
        Assertions.assertEquals(magazine.getId(), magazineDetails.getId());
    }
}
