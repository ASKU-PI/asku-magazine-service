package pl.asku.askumagazineservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.helpers.data.UserDataProvider;

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
                    magazineDataProvider.validMagazineDto().toBuilder().areaInMeters(area).build();
            String username = userDataProvider.getUser("test@test.pl").getId();
            magazineService.addMagazine(magazineDto, username, null);
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