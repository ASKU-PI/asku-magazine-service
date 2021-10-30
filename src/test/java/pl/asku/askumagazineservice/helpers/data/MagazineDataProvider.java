package pl.asku.askumagazineservice.helpers.data;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Heating;
import pl.asku.askumagazineservice.model.magazine.Light;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.magazine.MagazineType;
import pl.asku.askumagazineservice.service.MagazineService;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@AllArgsConstructor
public class MagazineDataProvider {

    MagazineService magazineService;

    public MagazineDto magazineDto() {
        return MagazineDto.builder()
                .title("Test test")
                .country("Poland")
                .city("Kraków")
                .street("Kawiory")
                .building("21")
                .longitude(BigDecimal.valueOf(5.0f))
                .latitude(BigDecimal.valueOf(5.0f))
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(10))
                .areaInMeters(BigDecimal.valueOf(60.0f))
                .pricePerMeter(BigDecimal.valueOf(100.0f))
                .minAreaToRent(BigDecimal.valueOf(10.0f))
                .type(MagazineType.GARAGE)
                .heating(Heating.ELECTRIC)
                .light(Light.NATURAL)
                .whole(false)
                .monitoring(false)
                .antiTheftDoors(true)
                .ventilation(true)
                .smokeDetectors(false)
                .selfService(false)
                .floor(2)
                .height(BigDecimal.valueOf(2.5f))
                .doorHeight(BigDecimal.valueOf(2.1f))
                .doorWidth(BigDecimal.valueOf(1.5f))
                .electricity(true)
                .parking(true)
                .vehicleManoeuvreArea(false)
                .ownerTransport(false)
                .description("Lorem ipsum")
                .build();
    }

    public MagazineDto mandatoryOnlyMagazineDto() {
        return MagazineDto.builder()
                .title("Test test")
                .country("Poland")
                .city("Kraków")
                .street("Kawiory")
                .building("21")
                .longitude(BigDecimal.valueOf(5.0f))
                .latitude(BigDecimal.valueOf(5.0f))
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(10))
                .areaInMeters(BigDecimal.valueOf(60.0f))
                .pricePerMeter(BigDecimal.valueOf(100.0f))
                .minAreaToRent(BigDecimal.valueOf(10.0f))
                .build();
    }

    public Magazine magazine(User user) throws LocationNotFoundException, LocationIqRequestFailedException {
        return magazine(user, magazineDto());
    }

    public Magazine magazine(User user, MagazineDto magazineDto) throws LocationNotFoundException,
            LocationIqRequestFailedException {
        return magazineService.addMagazine(magazineDto, user.getId(), null);
    }
}
