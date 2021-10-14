package pl.asku.askumagazineservice.helpers.data;

import org.springframework.stereotype.Component;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.model.Heating;
import pl.asku.askumagazineservice.model.Light;
import pl.asku.askumagazineservice.model.MagazineType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class MagazineDataProvider {

    public MagazineDto validMagazineDto() {
        return MagazineDto.builder()
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

    public String userIdentifier() {
        return "test";
    }

    public String otherUserIdentifier() {
        return "otherUser";
    }
}
