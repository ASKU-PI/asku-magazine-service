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
        return new MagazineDto(
                null,
                null,
                null,
                null,
                "Poland",
                "Kraków",
                "Kawiory",
                "21",
                BigDecimal.valueOf(5.0f),
                BigDecimal.valueOf(5.0f),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(10),
                BigDecimal.valueOf(60.0f),
                BigDecimal.valueOf(100.0f),
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
                BigDecimal.valueOf(2.5f),
                BigDecimal.valueOf(2.1f),
                BigDecimal.valueOf(1.5f),
                true,
                true,
                false,
                BigDecimal.valueOf(10.0f),
                false,
                "Lorem ipsum"
        );
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
}
