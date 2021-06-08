package pl.asku.askumagazineservice.service;

import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.repository.MagazineRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MagazineService {

    private final MagazineRepository magazineRepository;

    @Transactional
    public Magazine addMagazine(MagazineDto magazineDto, String username){
        Magazine magazine = Magazine.builder()
                .owner(username)
                .createdDate(new Date())
                .location(magazineDto.getLocation())
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

        return magazineRepository.save(magazine);
    }

    @Transactional(readOnly = true)
    public Optional<Magazine> getMagazineDetails(Long id){
        return magazineRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Magazine> searchMagazines(Predicate predicate, Integer page){
        return magazineRepository.findAll(predicate, PageRequest.of(page, 20)).toList();
    }

}
