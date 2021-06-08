package pl.asku.askumagazineservice.controller;

import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.dto.MagazinePreviewDto;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.service.MagazineService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class MagazineController {

    private final MagazineService magazineService;

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @PostMapping("/add")
    public ResponseEntity addMagazine(
            @RequestBody MagazineDto magazineDto,
            @RequestHeader("Username") Optional<String> username){
        System.out.println("dd");
        if(username.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You must pass Username in the header!");
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
                        magazine.get().getDescription()
                ));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MagazinePreviewDto>> searchMagazines(
            @RequestParam Optional<Integer> page,
            @QuerydslPredicate(root = Magazine.class) Predicate predicate
    ){
        List<Magazine> magazines = magazineService.searchMagazines(
                predicate,
                page.map(integer -> integer - 1).orElse(0)
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
                        magazine.getType()
                )).collect(Collectors.toList()));
    }
}
