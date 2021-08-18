package pl.asku.askumagazineservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.service.MagazineService;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class CheckAvailableTests {

    private final MagazineService magazineService;

    private final MagazineDto testMagazineDtoTemplate;

    @Autowired
    public CheckAvailableTests(MagazineService magazineService, MagazineDataProvider magazineDataProvider) {
        this.magazineService = magazineService;
        this.testMagazineDtoTemplate = magazineDataProvider.validMagazineDto();
    }

    @Test
    public void returnsTrueWhenNoOtherReservations(){
        //given
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        //when
        Float area = magazineDto.getMinAreaToRent() + 2.0f;
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
