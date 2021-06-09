package pl.asku.askumagazineservice.service;

import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.dto.ReservationDto;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.model.Reservation;
import pl.asku.askumagazineservice.repository.MagazineRepository;
import pl.asku.askumagazineservice.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MagazineService {

    private final MagazineRepository magazineRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public Magazine addMagazine(MagazineDto magazineDto, String username){
        Magazine magazine = Magazine.builder()
                .owner(username)
                .createdDate(LocalDate.now())
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
    public List<Magazine> searchMagazines(
            Predicate predicate, Integer page, LocalDate start, LocalDate end, Float area){
        return magazineRepository
                .findAll(predicate, PageRequest.of(page, 20))
                .stream()
                .filter(magazine -> checkIfMagazineAvailable(magazine, start, end, area))
                .collect(Collectors.toList());
    }

    @Transactional
    public Reservation addReservation(ReservationDto reservationDto, String username){
        Optional<Magazine> magazine = getMagazineDetails(reservationDto.getId());
        if(magazine.isEmpty() || reservationDto.getStartDate().compareTo(reservationDto.getEndDate()) > 0 ||
                !checkIfMagazineAvailable(
                magazine.get(),
                reservationDto.getStartDate(),
                reservationDto.getEndDate(),
                reservationDto.getAreaInMeters())){
            return null;
        }
        Reservation reservation = Reservation.builder()
                .createdDate(LocalDate.now())
                .user(username)
                .startDate(reservationDto.getStartDate())
                .endDate(reservationDto.getEndDate())
                .areaInMeters(reservationDto.getAreaInMeters())
                .build();
        return reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public boolean checkIfMagazineAvailable(Magazine magazine,
                                     LocalDate start, LocalDate end, Float area){
        if(magazine.getAreaInMeters() < area || magazine.getStartDate().compareTo(start) > 0 ||
            magazine.getEndDate().compareTo(end) < 0){
            return false;
        }
        List<Reservation> reservations = reservationRepository
                .findByMagazine_Id(magazine.getId())
                .stream()
                .filter(reservation -> (start.compareTo(reservation.getStartDate()) >= 0
                                        && start.compareTo(reservation.getEndDate()) <= 0) ||
                                        (end.compareTo(reservation.getStartDate()) >= 0
                                        && end.compareTo(reservation.getEndDate()) <= 0))
                .collect(Collectors.toList());
        if(reservations.isEmpty()){
            return true;
        }
        Float takenArea = reservations
                .stream()
                .map(Reservation::getAreaInMeters)
                .reduce(0.0f, Float::sum);
        return magazine.getAreaInMeters() - takenArea >= area;
    }

}
