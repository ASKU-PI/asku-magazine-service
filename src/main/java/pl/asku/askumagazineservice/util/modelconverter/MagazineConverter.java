package pl.asku.askumagazineservice.util.modelconverter;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.client.imageservice.MagazinePictureDto;
import pl.asku.askumagazineservice.dto.client.imageservice.PictureData;
import pl.asku.askumagazineservice.dto.magazine.MagazineDto;
import pl.asku.askumagazineservice.dto.magazine.MagazinePreviewDto;
import pl.asku.askumagazineservice.model.User;
import pl.asku.askumagazineservice.model.magazine.Geolocation;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.service.ReviewService;

@Service
@AllArgsConstructor
public class MagazineConverter {

  private final ImageServiceClient imageServiceClient;
  private final ReviewService reviewService;
  private final UserConverter userConverter;

  public MagazineDto toDto(Magazine magazine) {
    List<PictureData> photos;
    if (magazine.getId() == null) {
      photos = new ArrayList<>();
    } else {
      photos = imageServiceClient.getMagazinePictures(magazine.getId()).getPhotos();
    }

    return new MagazineDto(
        magazine.getId(),
        userConverter.toDto(magazine.getOwner()),
        magazine.getCreatedDate(),
        photos,
        magazine.getTitle(),
        magazine.getCountry(),
        magazine.getCity(),
        magazine.getStreet(),
        magazine.getBuilding(),
        magazine.getLocation(),
        reviewService.getMagazineReviewsNumber(magazine.getId()),
        reviewService.getMagazineAverageRating(magazine.getId()),
        magazine.getStartDate(),
        magazine.getEndDate(),
        magazine.getAreaInMeters(),
        magazine.getPricePerMeter(),
        magazine.getType(),
        magazine.getHeating(),
        magazine.getLight(),
        magazine.getWhole(),
        magazine.getMonitoring(),
        magazine.getAntiTheftDoors(),
        magazine.getVentilation(),
        magazine.getSmokeDetectors(),
        magazine.getSelfService(),
        magazine.getFloor(),
        magazine.getHeight(),
        magazine.getDoorHeight(),
        magazine.getDoorWidth(),
        magazine.getElectricity(),
        magazine.getParking(),
        magazine.getElevator(),
        magazine.getVehicleManoeuvreArea(),
        magazine.getMinAreaToRent(),
        magazine.getOwnerTransport(),
        magazine.getDescription(),
        magazine.getMinTemperature(),
        magazine.getMaxTemperature()
    );
  }

  public MagazinePreviewDto toPreviewDto(Magazine magazine) {
    MagazinePictureDto magazinePictureDto =
        imageServiceClient.getMagazinePictures(magazine.getId());

    return new MagazinePreviewDto(
        magazine.getId(),
        userConverter.toDto(magazine.getOwner()),
        magazine.getCreatedDate(),
        magazinePictureDto.getPhotos(),
        magazine.getTitle(),
        magazine.getCountry(),
        magazine.getCity(),
        magazine.getStreet(),
        magazine.getBuilding(),
        magazine.getLocation(),
        reviewService.getMagazineReviewsNumber(magazine.getId()),
        reviewService.getMagazineAverageRating(magazine.getId()),
        magazine.getStartDate(),
        magazine.getEndDate(),
        magazine.getAreaInMeters(),
        magazine.getPricePerMeter(),
        magazine.getType()
    );
  }

  public Magazine toMagazine(MagazineDto magazineDto, User user, Geolocation geolocation) {
    return Magazine.builder()
        .owner(user)
        .title(magazineDto.getTitle())
        .country(magazineDto.getCountry())
        .city(magazineDto.getCity())
        .street(magazineDto.getStreet())
        .building(magazineDto.getBuilding())
        .location(geolocation)
        .startDate(magazineDto.getStartDate())
        .endDate(magazineDto.getEndDate())
        .areaInMeters(magazineDto.getAreaInMeters())
        .pricePerMeter(magazineDto.getPricePerMeter())
        .type(magazineDto.getType())
        .heating(magazineDto.getHeating())
        .light(magazineDto.getLight())
        .whole(magazineDto.getWhole())
        .monitoring(magazineDto.getMonitoring())
        .antiTheftDoors(magazineDto.getAntiTheftDoors())
        .ventilation(magazineDto.getVentilation())
        .smokeDetectors(magazineDto.getSmokeDetectors())
        .selfService(magazineDto.getSelfService())
        .floor(magazineDto.getFloor())
        .height(magazineDto.getHeight())
        .doorHeight(magazineDto.getDoorHeight())
        .doorWidth(magazineDto.getDoorWidth())
        .electricity(magazineDto.getElectricity())
        .parking(magazineDto.getParking())
        .vehicleManoeuvreArea(magazineDto.getVehicleManoeuvreArea())
        .minAreaToRent(magazineDto.getMinAreaToRent())
        .ownerTransport(magazineDto.getOwnerTransport())
        .description(magazineDto.getDescription())
        .elevator(magazineDto.getElevator())
        .maxTemperature(magazineDto.getMaxTemperature())
        .minTemperature(magazineDto.getMinTemperature())
        .build();
  }

  public Magazine updateMagazine(Magazine magazine, MagazineDto magazineDto) {
    magazine.setTitle(magazineDto.getTitle());
    magazine.setHeating(magazineDto.getHeating());
    magazine.setLight(magazineDto.getLight());
    magazine.setMonitoring(magazineDto.getMonitoring());
    magazine.setAntiTheftDoors(magazineDto.getAntiTheftDoors());
    magazine.setVentilation(magazineDto.getVentilation());
    magazine.setSmokeDetectors(magazineDto.getSmokeDetectors());
    magazine.setSelfService(magazineDto.getSelfService());
    magazine.setFloor(magazineDto.getFloor());
    magazine.setHeight(magazineDto.getHeight());
    magazine.setDoorHeight(magazineDto.getDoorHeight());
    magazine.setDoorWidth(magazineDto.getDoorWidth());
    magazine.setElectricity(magazineDto.getElectricity());
    magazine.setParking(magazineDto.getParking());
    magazine.setVehicleManoeuvreArea(magazineDto.getVehicleManoeuvreArea());
    magazine.setMinAreaToRent(magazineDto.getMinAreaToRent());
    magazine.setOwnerTransport(magazineDto.getOwnerTransport());
    magazine.setDescription(magazineDto.getDescription());
    magazine.setElevator(magazineDto.getElevator());
    magazine.setMaxTemperature(magazineDto.getMaxTemperature());
    magazine.setMinTemperature(magazineDto.getMinTemperature());

    return magazine;
  }
}
