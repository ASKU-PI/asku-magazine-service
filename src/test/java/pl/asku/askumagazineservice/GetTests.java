package pl.asku.askumagazineservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.service.MagazineService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class GetTests {

    private final MagazineService magazineService;

    private final MagazineDto testMagazineDtoTemplate;

    @Autowired
    GetTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider) {
        this.magazineService = magazineService;
        this.testMagazineDtoTemplate = magazineDataProvider.validMagazineDto();
    }

    @Test
    public void getMagazineDetailsShouldReturnCorrectMagazine(){
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
