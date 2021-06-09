package pl.asku.askumagazineservice.controller;

import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.dto.MagazinePreviewDto;
import pl.asku.askumagazineservice.model.Heating;
import pl.asku.askumagazineservice.model.Light;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.model.MagazineType;
import pl.asku.askumagazineservice.service.MagazineService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class MagazineController {

    private final MagazineService magazineService;

    @PostMapping("/add")
    public ResponseEntity addMagazine(
            @RequestBody MagazineDto magazineDto,
            @RequestHeader("Username") Optional<String> username){
        if(username.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("No Username header");
        }
        Magazine magazine = magazineService.addMagazine(magazineDto, username.get());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Magazine created: " + magazine.getId());
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<MagazineDto> getMagazineDetails(
            @PathVariable Long id){
        Optional<Magazine> magazine = magazineService.getMagazineDetails(id);
        return magazine.isEmpty() ?
                ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(null) :
                ResponseEntity
                .status(HttpStatus.OK)
                .body(new MagazineDto(
                        magazine.get().getId(),
                        magazine.get().getOwner(),
                        magazine.get().getCreatedDate(),
                        magazine.get().getLocation(),
                        magazine.get().getStartDate(),
                        magazine.get().getEndDate(),
                        magazine.get().getAreaInMeters(),
                        magazine.get().getPricePerMeter(),
                        magazine.get().getType(),
                        magazine.get().getHeating(),
                        magazine.get().getLight(),
                        magazine.get().getWhole(),
                        magazine.get().getMonitoring(),
                        magazine.get().getAntiTheftDoors(),
                        magazine.get().getVentilation(),
                        magazine.get().getSmokeDetectors(),
                        magazine.get().getSelfService(),
                        magazine.get().getFloor(),
                        magazine.get().getHeight(),
                        magazine.get().getDoorHeight(),
                        magazine.get().getDoorWidth(),
                        magazine.get().getElectricity(),
                        magazine.get().getParking(),
                        magazine.get().getVehicleManoeuvreArea(),
                        magazine.get().getMinAreaToRent(),
                        magazine.get().getOwnerTransport(),
                        magazine.get().getDescription(),
                        magazine.get().getFreeSpace()
                ));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MagazinePreviewDto>> searchMagazines(
            @RequestParam Optional<Integer> page,
            @RequestParam String location,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam Float minArea,
            @RequestParam(required = false) Optional<Float> pricePerMeter,
            @RequestParam(required = false) Optional<MagazineType> type,
            @RequestParam(required = false) Optional<Heating> heating,
            @RequestParam(required = false) Optional<Light> light,
            @RequestParam(required = false) Optional<Boolean> whole,
            @RequestParam(required = false) Optional<Boolean> monitoring,
            @RequestParam(required = false) Optional<Boolean> antiTheftDoors,
            @RequestParam(required = false) Optional<Boolean> ventilation,
            @RequestParam(required = false) Optional<Boolean> smokeDetectors,
            @RequestParam(required = false) Optional<Boolean> selfService,
            @RequestParam(required = false) Optional<Boolean> floor,
            @RequestParam(required = false) Optional<Float> height,
            @RequestParam(required = false) Optional<Float> doorHeight,
            @RequestParam(required = false) Optional<Float> doorWidth,
            @RequestParam(required = false) Optional<Boolean> electricity,
            @RequestParam(required = false) Optional<Boolean> parking,
            @RequestParam(required = false) Optional<Boolean> vehicleManoeuvreArea,
            @RequestParam(required = false) Optional<Boolean> ownerTransport
            ){
        List<Magazine> magazines = magazineService.searchMagazines(
                page.map(integer -> integer - 1).orElse(0),
                location,
                start,
                end,
                minArea,
                pricePerMeter,
                type,
                heating,
                light,
                whole,
                monitoring,
                antiTheftDoors,
                ventilation,
                smokeDetectors,
                selfService,
                floor,
                height,
                doorHeight,
                doorWidth,
                electricity,
                parking,
                vehicleManoeuvreArea,
                ownerTransport
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(magazines.stream().map(magazine -> new MagazinePreviewDto(
                        magazine.getId(),
                        magazine.getOwner(),
                        magazine.getCreatedDate(),
                        magazine.getLocation(),
                        magazine.getStartDate(),
                        magazine.getEndDate(),
                        magazine.getAreaInMeters(),
                        magazine.getPricePerMeter(),
                        magazine.getType(),
                        magazine.getFreeSpace()
                )).collect(Collectors.toList()));
    }
}
