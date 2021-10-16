package pl.asku.askumagazineservice.magazine.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import pl.asku.askumagazineservice.client.GeocodingClient;
import pl.asku.askumagazineservice.client.ImageServiceClient;
import pl.asku.askumagazineservice.dto.MagazineDto;
import pl.asku.askumagazineservice.exception.LocationIqRequestFailedException;
import pl.asku.askumagazineservice.exception.LocationNotFoundException;
import pl.asku.askumagazineservice.model.Geolocation;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.model.search.MagazineFilters;
import pl.asku.askumagazineservice.repository.MagazineRepository;
import pl.asku.askumagazineservice.util.modelconverter.MagazineConverter;
import pl.asku.askumagazineservice.util.validator.MagazineValidator;

import javax.persistence.criteria.Predicate;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
@AllArgsConstructor
public class MagazineService {

    private final MagazineRepository magazineRepository;
    private final ImageServiceClient imageServiceClient;
    private final GeocodingClient geocodingClient;
    private final MagazineConverter magazineConverter;
    private final MagazineValidator magazineValidator;

    @Transactional
    public Magazine addMagazine(@Valid MagazineDto magazineDto, @NotNull @NotBlank String username,
                                MultipartFile[] photos)
            throws LocationNotFoundException, LocationIqRequestFailedException {
        magazineValidator.validate(magazineDto);

        Magazine magazine = magazineConverter.toMagazine(magazineDto);

        magazine.setOwner(username);

        Geolocation geolocation = geocodingClient.getGeolocation(
                magazine.getCountry(),
                magazine.getCity(),
                magazine.getStreet(),
                magazine.getBuilding()
        );

        magazine.setLongitude(geolocation.getLongitude());
        magazine.setLatitude(geolocation.getLatitude());

        if (photos != null && photos.length > 0)
            imageServiceClient.uploadMagazinePictures(magazine.getId(), photos);

        return magazineRepository.save(magazine);
    }

    public Optional<Magazine> getMagazineDetails(@NotNull Long id) {
        return magazineRepository.findById(id);
    }

    public List<Magazine> searchMagazines(
            @Min(1) Integer page,
            @NotNull MagazineFilters filters) {
        return findMagazinesWithSingleQuery(page, filters);
    }

    public BigDecimal getTotalPrice(@NotNull @Valid Magazine magazine, @NotNull LocalDate start, @NotNull LocalDate end,
                                    @NotNull @Min(0) BigDecimal area) {
        if (start.compareTo(end) >= 0)
            throw new ValidationException("End date must be greater than end date");

        int dateDifference = start.until(end).getDays();
        return area.multiply(magazine.getPricePerMeter()).multiply(BigDecimal.valueOf(dateDifference));
    }

    private List<Magazine> findMagazinesWithSingleQuery(Integer page, MagazineFilters filters) {
        //TODO: make this take reservations into account, allow multiple enum selections
        return magazineRepository
                .findAll(
                        (Specification<Magazine>) (root, criteriaQuery, criteriaBuilder) -> {
                            List<Predicate> predicates = new ArrayList<>();
                            if (filters.getStartDateGreaterOrEqual() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get(
                                        "startDate"), filters.getStartDateGreaterOrEqual())));
                            if (filters.getEndDateLessOrEqual() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get(
                                        "endDate"), filters.getEndDateLessOrEqual())));
                            if (filters.getMinFreeArea() != null) {
                                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get(
                                        "areaInMeters"), filters.getMinFreeArea())));
                                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get(
                                        "minAreaToRent"), filters.getMinFreeArea())));
                            }
                            if (filters.getLocationFilter().getMinLongitude() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThan(root.get("longitude"),
                                        filters.getLocationFilter().getMinLongitude())));
                            if (filters.getLocationFilter().getMaxLongitude() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThan(root.get("longitude"),
                                        filters.getLocationFilter().getMaxLongitude())));
                            if (filters.getLocationFilter().getMinLatitude() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThan(root.get("latitude"),
                                        filters.getLocationFilter().getMinLatitude())));
                            if (filters.getLocationFilter().getMaxLatitude() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThan(root.get("latitude"),
                                        filters.getLocationFilter().getMaxLatitude())));
                            if (filters.getMaxPricePerMeter() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get(
                                        "pricePerMeter"), filters.getMaxPricePerMeter())));
                            if (filters.getOwnerIdentifier() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("owner"),
                                        filters.getOwnerIdentifier())));
                            if (filters.getType() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("type"),
                                        filters.getType())));
                            if (filters.getHeating() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("heating"),
                                        filters.getHeating())));
                            if (filters.getLight() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("light"),
                                        filters.getLight())));
                            if (filters.getIsWhole() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("whole"),
                                        filters.getIsWhole())));
                            if (filters.getHasMonitoring() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("monitoring"),
                                        filters.getHasMonitoring())));
                            if (filters.getHasAntiTheftDoors() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("antiTheftDoors"),
                                        filters.getHasAntiTheftDoors())));
                            if (filters.getHasVentilation() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("ventilation"),
                                        filters.getHasVentilation())));
                            if (filters.getHasSmokeDetectors() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("smokeDetectors"),
                                        filters.getHasSmokeDetectors())));
                            if (filters.getIsSelfService() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("selfService"),
                                        filters.getIsSelfService())));
                            if (filters.getMinFloor() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get(
                                        "floor"), filters.getMinFloor())));
                            if (filters.getMaxFloor() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("floor"), filters.getMaxFloor())));
                            if (filters.getMinDoorHeight() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get(
                                        "doorHeight"), filters.getMinDoorHeight())));
                            if (filters.getMinDoorWidth() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get(
                                        "doorWidth"), filters.getMinDoorWidth())));
                            if (filters.getHasElectricity() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("electricity"),
                                        filters.getHasElectricity())));
                            if (filters.getHasParking() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("parking"),
                                        filters.getHasParking())));
                            if (filters.getHasVehicleManoeuvreArea() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get(
                                        "vehicleManoeuvreArea"), filters.getHasVehicleManoeuvreArea())));
                            if (filters.getCanOwnerTransport() != null)
                                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get(
                                        "vehicleManoeuvreArea"), filters.getCanOwnerTransport())));
                            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                        },
                        PageRequest.of(page - 1, 20))
                .stream()
                .collect(Collectors.toList());
    }

}
