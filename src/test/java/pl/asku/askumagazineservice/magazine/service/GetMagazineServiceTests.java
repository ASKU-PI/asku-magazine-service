package pl.asku.askumagazineservice.magazine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.model.Magazine;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetMagazineServiceTests extends MagazineServiceTestBase {

    @Autowired
    GetMagazineServiceTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider,
                            ImageServiceClient imageServiceClient) {
        super(magazineService, magazineDataProvider, imageServiceClient);
    }

    @Test
    public void getMagazineDetailsShouldReturnCorrectMagazine() throws LocationNotFoundException,
            LocationIqRequestFailedException {
        //given
        MagazineDto magazineDto = magazineDataProvider.validMagazineDto().toBuilder().build();
        String username = magazineDataProvider.userIdentifier();
        Magazine magazine = magazineService.addMagazine(magazineDto, username, null);

        //when
        Optional<Magazine> magazineDetails = magazineService.getMagazineDetails(magazine.getId());

        //then
        assertTrue(magazineDetails.isPresent());
        Assertions.assertEquals(magazine.getId(), magazineDetails.get().getId());
    }
}
