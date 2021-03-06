package pl.asku.askumagazineservice.helpers.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.magazine.MagazineCreateDto;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.dto.magazine.MagazineUpdateDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.exception.MagazineNotFoundException;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Geolocation;
import pl.asku.askumagazineservice.model.magazine.Heating;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.magazine.MagazineType;
import pl.asku.askumagazineservice.service.MagazineService;

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
        .location(Geolocation.builder().longitude(BigDecimal.valueOf(5.0f))
            .latitude(BigDecimal.valueOf(5.0f)).build())
        .startDate(LocalDate.now().plusDays(2))
        .endDate(LocalDate.now().plusDays(10))
        .areaInMeters(BigDecimal.valueOf(60.0f))
        .pricePerMeter(BigDecimal.valueOf(100.0f))
        .minAreaToRent(BigDecimal.valueOf(10.0f))
        .type(MagazineType.GARAGE)
        .heating(Heating.ELECTRIC)
        .light(true)
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

  public MagazineCreateDto magazineCreateDto() {
    return MagazineCreateDto.builder()
        .title("Test test")
        .country("Poland")
        .city("Kraków")
        .street("Kawiory")
        .building("21")
        .startDate(LocalDate.now().plusDays(2))
        .endDate(LocalDate.now().plusDays(10))
        .areaInMeters(BigDecimal.valueOf(60.0f))
        .pricePerMeter(BigDecimal.valueOf(100.0f))
        .minAreaToRent(BigDecimal.valueOf(10.0f))
        .type(MagazineType.GARAGE)
        .heating(Heating.ELECTRIC)
        .light(true)
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

  public MagazineUpdateDto magazineUpdateDto() {
    return MagazineUpdateDto.builder()
        .title("Test test")
        .available(true)
        .endDate(LocalDate.now().plusDays(10))
        .areaInMeters(BigDecimal.valueOf(60.0f))
        .pricePerMeter(BigDecimal.valueOf(100.0f))
        .minAreaToRent(BigDecimal.valueOf(10.0f))
        .type(MagazineType.GARAGE)
        .heating(Heating.ELECTRIC)
        .light(true)
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
        .location(Geolocation.builder().longitude(BigDecimal.valueOf(5.0f))
            .latitude(BigDecimal.valueOf(5.0f)).build())
        .startDate(LocalDate.now().plusDays(2))
        .endDate(LocalDate.now().plusDays(10))
        .areaInMeters(BigDecimal.valueOf(60.0f))
        .pricePerMeter(BigDecimal.valueOf(100.0f))
        .minAreaToRent(BigDecimal.valueOf(10.0f))
        .build();
  }

  public Magazine magazine(User user)
      throws LocationNotFoundException, LocationIqRequestFailedException {
    return magazine(user, magazineCreateDto());
  }

  public Magazine magazine(User user, MagazineCreateDto magazineDto) throws LocationNotFoundException,
      LocationIqRequestFailedException {
    return magazineService.addMagazine(magazineDto, user, null);
  }

  public Magazine deletedMagazine(User user)
      throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotFoundException {
    Magazine magazine = magazine(user);
    return magazineService.deleteMagazine(magazine);
  }

  public Magazine deletedMagazine(User user, MagazineCreateDto magazineDto)
      throws LocationNotFoundException, LocationIqRequestFailedException,
      MagazineNotFoundException {
    Magazine magazine = magazine(user, magazineDto);
    return magazineService.deleteMagazine(magazine);
  }
}
