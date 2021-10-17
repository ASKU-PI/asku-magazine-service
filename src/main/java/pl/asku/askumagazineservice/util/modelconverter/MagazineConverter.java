package pl.asku.askumagazineservice.util.modelconverter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.dto.MagazinePreviewDto;
import pl.asku.askumagazineservice.dto.imageservice.MagazinePictureDto;
import pl.asku.askumagazineservice.dto.imageservice.PictureData;
import pl.asku.askumagazineservice.model.Magazine;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MagazineConverter {

    private final ImageServiceClient imageServiceClient;

    public MagazineDto toDto(Magazine magazine) {
        List<PictureData> photos;
        if (magazine.getId() == null)
            photos = new ArrayList<>();
        else
            photos = imageServiceClient.getMagazinePictures(magazine.getId()).getPhotos();

        return new MagazineDto(
                magazine.getId(),
                magazine.getOwner(),
                magazine.getCreatedDate(),
                photos,
                magazine.getCountry(),
                magazine.getCity(),
                magazine.getStreet(),
                magazine.getBuilding(),
                magazine.getLongitude(),
                magazine.getLatitude(),
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
                magazine.getVehicleManoeuvreArea(),
                magazine.getMinAreaToRent(),
                magazine.getOwnerTransport(),
                magazine.getDescription()
        );
    }

    public MagazinePreviewDto toPreviewDto(Magazine magazine) {
        MagazinePictureDto magazinePictureDto = imageServiceClient.getMagazinePictures(magazine.getId());

        return new MagazinePreviewDto(
                magazine.getId(),
                magazine.getOwner(),
                magazine.getCreatedDate(),
                magazinePictureDto.getPhotos(),
                magazine.getCountry(),
                magazine.getCity(),
                magazine.getStreet(),
                magazine.getBuilding(),
                magazine.getLongitude(),
                magazine.getLatitude(),
                magazine.getStartDate(),
                magazine.getEndDate(),
                magazine.getAreaInMeters(),
                magazine.getPricePerMeter(),
                magazine.getType()
        );
    }

    public Magazine toMagazine(MagazineDto magazineDto) {
        return Magazine.builder()
                .owner(magazineDto.getOwner())
                .country(magazineDto.getCountry())
                .city(magazineDto.getCity())
                .street(magazineDto.getStreet())
                .building(magazineDto.getBuilding())
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
                .build();
    }
}
