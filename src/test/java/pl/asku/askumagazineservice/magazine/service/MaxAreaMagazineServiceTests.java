package pl.asku.askumagazineservice.magazine.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.service.MagazineService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MaxAreaMagazineServiceTests extends MagazineServiceTestBase {

    @Autowired
    public MaxAreaMagazineServiceTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider,
                                       ImageServiceClient imageServiceClient, UserDataProvider userDataProvider) {
        super(magazineService, magazineDataProvider, imageServiceClient, userDataProvider);
    }

    @Test
    public void returnsCorrectValue()
            throws LocationNotFoundException, LocationIqRequestFailedException, MagazineNotFoundException {
        //given
        List<BigDecimal> areas = List.of(
                BigDecimal.valueOf(30.0f),
                BigDecimal.valueOf(200.0f),
                BigDecimal.valueOf(200.0f),
                BigDecimal.valueOf(50.0f),
                BigDecimal.valueOf(85.0f)
        );

        BigDecimal maxArea = Collections.max(areas);

        for (BigDecimal area : areas) {
            MagazineDto magazineDto =
                    magazineDataProvider.magazineDto().toBuilder().areaInMeters(area).build();
            User user = userDataProvider.user("test@test.pl", "666666666");
            magazineDataProvider.magazine(user, magazineDto);
        }

        //when
        BigDecimal returnedMaxArea = magazineService.maxArea();

        //then
        assertEquals(0, maxArea.compareTo(returnedMaxArea));
    }

    @Test
    public void failsWhenNoMagazines() {
        //when then
        assertThrows(MagazineNotFoundException.class, magazineService::maxArea);
    }
}
