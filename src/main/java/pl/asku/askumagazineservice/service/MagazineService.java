package pl.asku.askumagazineservice.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.asku.askumagazineservice.client.GeocodingClient;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.dto.ReservationDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.model.*;
import pl.asku.askumagazineservice.model.search.MagazineFilters;
import pl.asku.askumagazineservice.repository.MagazineRepository;
import pl.asku.askumagazineservice.repository.ReservationRepository;

import javax.persistence.criteria.Predicate;
import javax.validation.ValidationException;
import java.math.BigDecimal;
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
    private final GeocodingClient geocodingClient;

    @Transactional
    public Magazine addMagazine(MagazineDto magazineDto, String username) throws LocationNotFoundException, LocationIqRequestFailedException {
        List<String> violationMessages = magazineDto.getViolationMessages();
        if (violationMessages.size() > 0) {
            throw new ValidationException(violationMessages.toString());
        }

        Magazine magazine = magazineDto.toMagazine(username);

        Geolocation geolocation = geocodingClient.getGeolocation(
                magazine.getCountry(),
                magazine.getCity(),
                magazine.getStreet(),
                magazine.getBuilding()
        );

        magazine.setLongitude(geolocation.getLongitude());
        magazine.setLatitude(geolocation.getLatitude());

        return magazineRepository.save(magazine);
    }

    public Optional<Magazine> getMagazineDetails(Long id) {
        return magazineRepository.findById(id);
    }

    public List<Magazine> getUserMagazines(String username, Integer page) {
        return magazineRepository.findAllByOwner(username, PageRequest.of(page, 20));
    }

    public List<Magazine> searchMagazines(
            Integer page,
            MagazineFilters filters) {
        //TODO: make this take reservations into account
        return magazineRepository
                .findAll(
                        (Specification<Magazine>) (root, criteriaQuery, criteriaBuilder) -> {
                            List<Predicate> predicates = new ArrayList<>();
                            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), filters.getStartDateGreaterOrEqual())));
                            predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), filters.getEndDateLessOrEqual())));
                            predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("areaInMeters"), filters.getMinFreeArea())));
                            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("areaInMeters"), filters.getMaxFreeArea())));
                            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("minAreaToRent"), filters.getMinFreeArea())));
                            predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThan(root.get("longitude"), filters.getLocationFilter().getMinLongitude())));
                            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThan(root.get("longitude"), filters.getLocationFilter().getMaxLongitude())));
                            predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThan(root.get("latitude"), filters.getLocationFilter().getMinLatitude())));
                            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThan(root.get("latitude"), filters.getLocationFilter().getMaxLatitude())));
                            if(filters.getMaxPricePerMeter() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("pricePerMeter"), filters.getMaxPricePerMeter())));
                            if(filters.getType() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("type"), filters.getType())));
                            if(filters.getHeating() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("heating"), filters.getHeating())));
                            if(filters.getLight() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("light"), filters.getLight())));
                            if(filters.getIsWhole() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("whole"), filters.getIsWhole())));
                            if(filters.getHasMonitoring() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("monitoring"), filters.getHasMonitoring())));
                            if(filters.getHasAntiTheftDoors() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("antiTheftDoors"), filters.getHasAntiTheftDoors())));
                            if(filters.getHasVentilation() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("ventilation"), filters.getHasVentilation())));
                            if(filters.getHasSmokeDetectors() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("smokeDetectors"), filters.getHasSmokeDetectors())));
                            if(filters.getIsSelfService() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("selfService"), filters.getIsSelfService())));
                            if(filters.getMinFloor() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("floor"), filters.getMinFloor())));
                            if(filters.getMaxFloor() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("floor"), filters.getMaxFloor())));
                            if(filters.getMinDoorHeight() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("doorHeight"), filters.getMinDoorHeight())));
                            if(filters.getMinDoorWidth() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("doorWidth"), filters.getMinDoorWidth())));
                            if(filters.getHasElectricity() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("electricity"), filters.getHasElectricity())));
                            if(filters.getHasParking() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("parking"), filters.getHasParking())));
                            if(filters.getHasVehicleManoeuvreArea() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("vehicleManoeuvreArea"), filters.getHasVehicleManoeuvreArea())));
                            if(filters.getCanOwnerTransport() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("vehicleManoeuvreArea"), filters.getCanOwnerTransport())));
                            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                        },
                        PageRequest.of(page, 20))
                .stream()
                .collect(Collectors.toList());
    }

    public Optional<Reservation> addReservation(ReservationDto reservationDto, String username) {
        Optional<Magazine> magazine = getMagazineDetails(reservationDto.getMagazineId());
        if (magazine.isEmpty()) return Optional.empty();
        return addReservation(magazine.get(), reservationDto, username);
    }

    @Transactional
    public Optional<Reservation> addReservation(Magazine magazine, ReservationDto reservationDto, String username) {
        if (reservationDto.getStartDate().compareTo(reservationDto.getEndDate()) > 0 ||
                !checkIfMagazineAvailable(
                        magazine,
                        reservationDto.getStartDate(),
                        reservationDto.getEndDate(),
                        reservationDto.getAreaInMeters())) {
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

    public boolean checkIfMagazineAvailable(Magazine magazine,
                                            LocalDate start, LocalDate end, BigDecimal area) {
        if (magazine.getAreaInMeters().compareTo(area) < 0 || magazine.getMinAreaToRent().compareTo(area) > 0
                || magazine.getStartDate().compareTo(start) > 0 ||
                magazine.getEndDate().compareTo(end) < 0) {
            return false;
        }
        BigDecimal takenArea = getTakenArea(magazine.getId(), start, end);
        return magazine.getAreaInMeters().subtract(takenArea).compareTo(area) >= 0;
    }

    private BigDecimal getTakenArea(Long magazineId, LocalDate start, LocalDate end) {
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
        if (reservations.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return reservations
                .stream()
                .map(Reservation::getAreaInMeters)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalPrice(Magazine magazine, LocalDate start, LocalDate end, BigDecimal area) {
        int dateDifference = start.until(end).getDays();
        return area.multiply(magazine.getPricePerMeter()).multiply(BigDecimal.valueOf(dateDifference));
    }

}
