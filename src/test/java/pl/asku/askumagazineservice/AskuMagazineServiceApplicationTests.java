package pl.asku.askumagazineservice;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.model.Heating;
import pl.asku.askumagazineservice.model.Light;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.model.MagazineType;
import pl.asku.askumagazineservice.repository.MagazineRepository;
import pl.asku.askumagazineservice.service.MagazineService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AskuMagazineServiceApplicationTests {

    private final MagazineService magazineService;
    private final MagazineRepository magazineRepository;

    private final MagazineDto testMagazineDtoTemplate = new MagazineDto(
            null,
            null,
            null,
            "London",
            LocalDate.now().plusDays(2),
            LocalDate.now().plusDays(10),
            60.0f,
            100.0f,
            MagazineType.GARAGE,
            Heating.ELECTRIC,
            Light.NATURAL,
            false,
            false,
            true,
            true,
            false,
            false,
            2,
            2.5f,
            2.1f,
            1.5f,
            true,
            true,
            false,
            10.0f,
            false,
            "Lorem ipsum"
    );

    @Autowired
    AskuMagazineServiceApplicationTests(MagazineService magazineService, MagazineRepository magazineRepository) {
        this.magazineService = magazineService;
        this.magazineRepository = magazineRepository;
    }

    @Test
    public void addMagazineShouldAddToDatabase(){
        //given
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        //when
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        Optional<Magazine> magazineFromDb = magazineRepository.findById(magazine.getId());

        //then
        assertTrue(magazineFromDb.isPresent());
        Assertions.assertEquals(magazine, magazineFromDb.get());
    }

    @Test
    public void addMagazineShouldReturnCorrectMagazine(){
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
    public void getMagazineDetailsShouldReturnCorrectMagazine(){
        //given
        MagazineDto magazineDto = testMagazineDtoTemplate.toBuilder().build();
        String username = "test";

        //when
        Magazine magazine = magazineService.addMagazine(magazineDto, username);
        Optional<Magazine> magazineDetails = magazineService.getMagazineDetails(magazine.getId());

        //then
        assertTrue(magazineDetails.isPresent());
        Assertions.assertEquals(magazine, magazineDetails.get());
    }

}
