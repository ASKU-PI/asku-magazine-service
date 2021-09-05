package pl.asku.askumagazineservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.helpers.data.MagazineDataProvider;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.repository.MagazineRepository;
import pl.asku.askumagazineservice.service.MagazineService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AddTests {
    private final MagazineService magazineService;
    private final MagazineRepository magazineRepository;

    private final MagazineDto testMagazineDtoTemplate;
    private final MagazineDto testMagazineDtoMandatoryOnlyTemplate;

    @Autowired
    AddTests(MagazineService magazineService, MagazineRepository magazineRepository, MagazineDataProvider magazineDataProvider) {
        this.magazineService = magazineService;
        this.magazineRepository = magazineRepository;
        this.testMagazineDtoTemplate = magazineDataProvider.validMagazineDto();
        this.testMagazineDtoMandatoryOnlyTemplate = magazineDataProvider.mandatoryOnlyMagazineDto();
    }

    @Test
    public void shouldAddToDatabase(){
        //given
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        //when
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        Optional<Magazine> magazineFromDb = magazineRepository.findById(magazine.getId());

        //then
        assertTrue(magazineFromDb.isPresent());
        Assertions.assertEquals(magazine.getId(), magazineFromDb.get().getId());
    }

    @Test
    public void shouldReturnCorrectMagazine(){
        //given
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        //when
        Magazine magazine = magazineService.addMagazine(magazineDto, username);

        //then
        Assertions.assertAll(
                () -> assertNotNull(magazine.getId()),
                () -> assertEquals(username, magazine.getOwner()),
                () -> assertNotNull(magazine.getCreatedDate()),
                () -> assertEquals(magazineDto.getLocation(), magazine.getLocation()),
                () -> assertEquals(magazineDto.getStartDate(), magazine.getStartDate()),
                () -> assertEquals(magazineDto.getEndDate(), magazine.getEndDate()),
                () -> assertEquals(magazineDto.getAreaInMeters(), magazine.getAreaInMeters()),
                () -> assertEquals(magazineDto.getPricePerMeter(), magazine.getPricePerMeter()),
                () -> assertEquals(magazineDto.getType(), magazine.getType()),
                () -> assertEquals(magazineDto.getHeating(), magazine.getHeating()),
                () -> assertEquals(magazineDto.getLight(), magazine.getLight()),
                () -> assertEquals(magazineDto.getWhole(), magazine.getWhole()),
                () -> assertEquals(magazineDto.getMonitoring(), magazine.getMonitoring()),
                () -> assertEquals(magazineDto.getAntiTheftDoors(), magazine.getAntiTheftDoors()),
                () -> assertEquals(magazineDto.getVentilation(), magazine.getVentilation()),
                () -> assertEquals(magazineDto.getSmokeDetectors(), magazine.getSmokeDetectors()),
                () -> assertEquals(magazineDto.getSelfService(), magazine.getSelfService()),
                () -> assertEquals(magazineDto.getFloor(), magazine.getFloor()),
                () -> assertEquals(magazineDto.getHeight(), magazine.getHeight()),
                () -> assertEquals(magazineDto.getDoorHeight(), magazine.getDoorHeight()),
                () -> assertEquals(magazineDto.getDoorWidth(), magazine.getDoorWidth()),
                () -> assertEquals(magazineDto.getElectricity(), magazine.getElectricity()),
                () -> assertEquals(magazineDto.getParking(), magazine.getParking()),
                () -> assertEquals(magazineDto.getVehicleManoeuvreArea(), magazine.getVehicleManoeuvreArea()),
                () -> assertEquals(magazineDto.getMinAreaToRent(), magazine.getMinAreaToRent()),
                () -> assertEquals(magazineDto.getOwnerTransport(), magazine.getOwnerTransport()),
                () -> assertEquals(magazineDto.getDescription(), magazine.getDescription())
        );
    }

    @Test
    public void succeedsForOnlyMandatoryFields() {
        //given
        MagazineDto magazineDto = testMagazineDtoMandatoryOnlyTemplate.toBuilder().build();
        String username = "test";

        //when
        Magazine magazine = magazineService.addMagazine(magazineDto, username);

        //then
        Assertions.assertAll(
                () -> assertNotNull(magazine.getId()),
                () -> assertEquals(username, magazine.getOwner()),
                () -> assertNotNull(magazine.getCreatedDate()),
                () -> assertEquals(magazineDto.getLocation(), magazine.getLocation()),
                () -> assertEquals(magazineDto.getStartDate(), magazine.getStartDate()),
                () -> assertEquals(magazineDto.getEndDate(), magazine.getEndDate()),
                () -> assertEquals(magazineDto.getAreaInMeters(), magazine.getAreaInMeters()),
                () -> assertEquals(magazineDto.getPricePerMeter(), magazine.getPricePerMeter()),
                () -> assertEquals(magazineDto.getType(), magazine.getType()),
                () -> assertEquals(magazineDto.getHeating(), magazine.getHeating()),
                () -> assertEquals(magazineDto.getLight(), magazine.getLight()),
                () -> assertEquals(magazineDto.getWhole(), magazine.getWhole()),
                () -> assertEquals(magazineDto.getMonitoring(), magazine.getMonitoring()),
                () -> assertEquals(magazineDto.getAntiTheftDoors(), magazine.getAntiTheftDoors()),
                () -> assertEquals(magazineDto.getVentilation(), magazine.getVentilation()),
                () -> assertEquals(magazineDto.getSmokeDetectors(), magazine.getSmokeDetectors()),
                () -> assertEquals(magazineDto.getSelfService(), magazine.getSelfService()),
                () -> assertEquals(magazineDto.getFloor(), magazine.getFloor()),
                () -> assertEquals(magazineDto.getHeight(), magazine.getHeight()),
                () -> assertEquals(magazineDto.getDoorHeight(), magazine.getDoorHeight()),
                () -> assertEquals(magazineDto.getDoorWidth(), magazine.getDoorWidth()),
                () -> assertEquals(magazineDto.getElectricity(), magazine.getElectricity()),
                () -> assertEquals(magazineDto.getParking(), magazine.getParking()),
                () -> assertEquals(magazineDto.getVehicleManoeuvreArea(), magazine.getVehicleManoeuvreArea()),
                () -> assertEquals(magazineDto.getMinAreaToRent(), magazine.getMinAreaToRent()),
                () -> assertEquals(magazineDto.getOwnerTransport(), magazine.getOwnerTransport()),
                () -> assertEquals(magazineDto.getDescription(), magazine.getDescription())
        );
    }

    @Test
    public void failsForEmptyMandatoryFields() {
        //given
        MagazineDto magazineDto = testMagazineDtoMandatoryOnlyTemplate.toBuilder()
                .location("")
                .build();
        String username = "test";

        //when
        assertThrows(RuntimeException.class, () -> magazineService.addMagazine(magazineDto, username));
    }
}
