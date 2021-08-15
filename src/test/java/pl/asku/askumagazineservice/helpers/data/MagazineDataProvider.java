package pl.asku.askumagazineservice.helpers.data;

import org.springframework.stereotype.Component;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.model.Heating;
import pl.asku.askumagazineservice.model.Light;
import pl.asku.askumagazineservice.model.MagazineType;

import java.time.LocalDate;

@Component
public class MagazineDataProvider {

    public MagazineDto validMagazineDto() {
        return new MagazineDto(
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
                "Lorem ipsum",
                null,
                null
        );
    }
}
