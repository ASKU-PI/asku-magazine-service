package pl.asku.askumagazineservice.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.dto.ReservationDto;
import pl.asku.askumagazineservice.model.*;
import pl.asku.askumagazineservice.repository.MagazineRepository;
import pl.asku.askumagazineservice.repository.ReservationRepository;

import javax.persistence.criteria.Predicate;
import javax.validation.*;
import java.time.LocalDate;
import java.util.ArrayList;
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
        List<String> violationMessages = magazineDto.getViolationMessages();
        if (violationMessages.size() > 0) {
            throw new ValidationException(violationMessages.toString());
        }

        Magazine magazine = magazineDto.toMagazine(username);

        return magazineRepository.save(magazine);
    }

    @Transactional(readOnly = true)
    public Optional<Magazine> getMagazineDetails(Long id){
        return magazineRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Magazine> searchMagazines(
            Integer page,
            String location,
            LocalDate start,
            LocalDate end,
            Float area,
            Optional<Float> pricePerMeter,
            Optional<MagazineType> type,
            Optional<Heating> heating,
            Optional<Light> light,
            Optional<Boolean> whole,
            Optional<Boolean> monitoring,
            Optional<Boolean> antiTheftDoors,
            Optional<Boolean> ventilation,
            Optional<Boolean> smokeDetectors,
            Optional<Boolean> selfService,
            Optional<Boolean> floor,
            Optional<Float> height,
            Optional<Float> doorHeight,
            Optional<Float> doorWidth,
            Optional<Boolean> electricity,
            Optional<Boolean> parking,
            Optional<Boolean> vehicleManoeuvreArea,
            Optional<Boolean> ownerTransport){
        //TODO: make this take reservations into account
        return magazineRepository
                .findAll(
                        (Specification<Magazine>) (root, criteriaQuery, criteriaBuilder) -> {
                            List<Predicate> predicates = new ArrayList<>();
                            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), start)));
                            predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), end)));
                            predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("areaInMeters"), area)));
                            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("minAreaToRent"), area)));
                            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("location"), location)));
                            pricePerMeter.ifPresent(aFloat -> predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("pricePerMeter"), aFloat))));
                            type.ifPresent(magazineType -> predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("type"), magazineType))));
                            heating.ifPresent(aHeating -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("heating"), aHeating)))));
                            light.ifPresent(aLight -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("light"), aLight)))));
                            whole.ifPresent(b -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("whole"), b)))));
                            monitoring.ifPresent(b -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("monitoring"), b)))));
                            antiTheftDoors.ifPresent(b -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("antiTheftDoors"), b)))));
                            ventilation.ifPresent(b -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("ventilation"), b)))));
                            smokeDetectors.ifPresent(b -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("smokeDetectors"), b)))));
                            selfService.ifPresent(b -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("selfService"), b)))));
                            floor.ifPresent(b -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("floor"), b)))));
                            height.ifPresent(b -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("height"), b)))));
                            doorHeight.ifPresent(b -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("doorHeight"), b)))));
                            doorWidth.ifPresent(b -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("doorWidth"), b)))));
                            electricity.ifPresent(b -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("electricity"), b)))));
                            parking.ifPresent(b -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("parking"), b)))));
                            vehicleManoeuvreArea.ifPresent(b -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("vehicleManoeuvreArea"), b)))));
                            ownerTransport.ifPresent(b -> predicates.add(criteriaBuilder.and((criteriaBuilder.equal(root.get("ownerTransport"), b)))));
                            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                        },
                        PageRequest.of(page, 20))
                .stream()
                .collect(Collectors.toList());
    }

    public Optional<Reservation> addReservation(ReservationDto reservationDto, String username){
        Optional<Magazine> magazine = getMagazineDetails(reservationDto.getMagazineId());
        if(magazine.isEmpty()) return Optional.empty();
        return addReservation(magazine.get(), reservationDto, username);
    }

    @Transactional
    public Optional<Reservation> addReservation(Magazine magazine, ReservationDto reservationDto, String username){
        if(reservationDto.getStartDate().compareTo(reservationDto.getEndDate()) > 0 ||
                !checkIfMagazineAvailable(
                magazine,
                reservationDto.getStartDate(),
                reservationDto.getEndDate(),
                reservationDto.getAreaInMeters())){
            return Optional.empty();
        }
        Reservation reservation = Reservation.builder()
                .createdDate(LocalDate.now())
                .user(username)
                .startDate(reservationDto.getStartDate())
                .endDate(reservationDto.getEndDate())
                .areaInMeters(reservationDto.getAreaInMeters())
                .magazine(magazine)
                .build();
        return Optional.of(reservationRepository.save(reservation));
    }

    @Transactional(readOnly = true)
    public boolean checkIfMagazineAvailable(Magazine magazine,
                                     LocalDate start, LocalDate end, Float area){
        if(magazine.getAreaInMeters() < area || magazine.getMinAreaToRent() > area
                || magazine.getStartDate().compareTo(start) > 0 ||
            magazine.getEndDate().compareTo(end) < 0){
            return false;
        }
        Float takenArea = getTakenArea(magazine.getId(), start, end);
        return magazine.getAreaInMeters() - takenArea >= area;
    }

    private Float getTakenArea(Long magazineId, LocalDate start, LocalDate end){
        List<Reservation> reservations = reservationRepository
                .findByMagazine_Id(magazineId)
                .stream()
                .filter(reservation -> (start.compareTo(reservation.getStartDate()) >= 0
                        && start.compareTo(reservation.getEndDate()) <= 0) ||
                        (end.compareTo(reservation.getStartDate()) >= 0
                                && end.compareTo(reservation.getEndDate()) <= 0) ||
                        (start.compareTo(reservation.getStartDate()) <= 0
                                && end.compareTo(reservation.getEndDate()) >= 0) ||
                        (start.compareTo(reservation.getStartDate()) >= 0
                                && end.compareTo(reservation.getEndDate()) <= 0))
                .collect(Collectors.toList());
        if(reservations.isEmpty()){
            return 0.0f;
        }
        return reservations
                .stream()
                .map(Reservation::getAreaInMeters)
                .reduce(0.0f, Float::sum);
    }

}
