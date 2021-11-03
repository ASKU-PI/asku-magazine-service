package pl.asku.askumagazineservice.repository.magazine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.magazine.search.MagazineFilters;
import pl.asku.askumagazineservice.model.magazine.search.MagazineSearchResult;

public class CustomMagazineRepositoryImpl implements CustomMagazineRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public MagazineSearchResult search(MagazineFilters magazineFilters, PageRequest pageRequest)
      throws UserNotFoundException {
    TypedQuery<QueryResult> query = createQuery(magazineFilters, pageRequest.getSort());
    int recordsCount = query.getResultList().size();
    Integer pages = new BigDecimal(recordsCount).divide(new BigDecimal(pageRequest.getPageSize()),
        RoundingMode.UP).intValue();
    query.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize());
    query.setMaxResults(pageRequest.getPageSize());
    List<Magazine> magazines =
        query.getResultList().stream().map(queryResult -> queryResult.magazine)
            .collect(Collectors.toList());
    return MagazineSearchResult.builder()
        .mapCenter(magazineFilters.getLocationFilter() != null
            ? magazineFilters.getLocationFilter().getMapCenter()
            : null)
        .spaces(magazines)
        .records(recordsCount)
        .pages(pages)
        .build();
  }

  private TypedQuery<QueryResult> createQuery(MagazineFilters magazineFilters, Sort sortBy)
      throws UserNotFoundException {
    LocalDate today = LocalDate.now();

    StringBuilder queryBuilder =
        new StringBuilder(
            "SELECT NEW pl.asku.askumagazineservice.repository.magazine.QueryResult(m, SUM(r"
                + ".areaInMeters)) FROM Magazine m LEFT JOIN Reservation r ON r.magazine = m.id "
                + "WHERE 1 = 1 "
                + "AND");

    if (magazineFilters.getLocationFilter() != null) {
      if (magazineFilters.getLocationFilter().getMaxLatitude() != null) {
        queryBuilder
            .append(" m.location.latitude <= ")
            .append(magazineFilters.getLocationFilter().getMaxLatitude().toString())
            .append(" AND");
      }

      if (magazineFilters.getLocationFilter().getMinLatitude() != null) {
        queryBuilder
            .append(" m.location.latitude >= ")
            .append(magazineFilters.getLocationFilter().getMinLatitude().toString())
            .append(" AND");
      }

      if (magazineFilters.getLocationFilter().getMaxLongitude() != null) {
        queryBuilder
            .append(" m.location.longitude <= ")
            .append(magazineFilters.getLocationFilter().getMaxLongitude().toString())
            .append(" AND");
      }

      if (magazineFilters.getLocationFilter().getMinLongitude() != null) {
        queryBuilder
            .append(" m.location.longitude >= ")
            .append(magazineFilters.getLocationFilter().getMinLongitude().toString())
            .append(" AND");
      }
    }

    if (magazineFilters.getWithDeleted() == null || !magazineFilters.getWithDeleted()) {
      queryBuilder.append(" m.deleted = false AND");
    }

    if (magazineFilters.getStartDateGreaterOrEqual() != null) {
      queryBuilder
          .append(" m.startDate <= TO_DATE('")
          .append(magazineFilters.getStartDateGreaterOrEqual())
          .append("','yyyy-MM-dd') AND");
    }

    if (magazineFilters.getEndDateLessOrEqual() != null) {
      queryBuilder
          .append(" m.endDate >= TO_DATE('")
          .append(magazineFilters.getEndDateLessOrEqual())
          .append("','yyyy-MM-dd') AND");
    }

    if (magazineFilters.getMaxFreeArea() != null && magazineFilters.getIsWhole() != null
        && magazineFilters.getIsWhole()) {
      queryBuilder
          .append(" m.areaInMeters <= ")
          .append(magazineFilters.getMaxFreeArea().toString())
          .append(" AND");
    }

    if (magazineFilters.getMinFreeArea() != null) {
      queryBuilder
          .append(" m.areaInMeters >= ")
          .append(magazineFilters.getMinFreeArea().toString())
          .append(" AND");
    }

    if (magazineFilters.getMinFreeArea() != null) {
      queryBuilder
          .append(" m.minAreaToRent <= ")
          .append(magazineFilters.getMinFreeArea().toString())
          .append(" AND");
    }

    if (magazineFilters.getMaxPricePerMeter() != null) {
      queryBuilder
          .append(" m.pricePerMeter <= ")
          .append(magazineFilters.getMaxPricePerMeter().toString())
          .append(" AND");
    }

    if (magazineFilters.getMinPricePerMeter() != null) {
      queryBuilder
          .append(" m.pricePerMeter >= ")
          .append(magazineFilters.getMinPricePerMeter().toString())
          .append(" AND");
    }

    if (magazineFilters.getOwnerIdentifier() != null) {
      if (!isUserIdentifier(magazineFilters.getOwnerIdentifier())) {
        throw new UserNotFoundException();
      }
      queryBuilder
          .append(" m.owner = '")
          .append(magazineFilters.getOwnerIdentifier())
          .append("' AND");
    }

    if (magazineFilters.getType() != null) {
      queryBuilder
          .append(" m.type = '")
          .append(magazineFilters.getType().toString())
          .append("' AND");
    }

    if (magazineFilters.getHeating() != null) {
      queryBuilder
          .append(" m.heating = '")
          .append(magazineFilters.getHeating().toString())
          .append("' AND");
    }

    if (magazineFilters.getLight() != null) {
      queryBuilder
          .append(" m.light = '")
          .append(magazineFilters.getLight().toString())
          .append("' AND");
    }

    if (magazineFilters.getIsWhole() != null) {
      queryBuilder
          .append(" m.whole = ")
          .append(magazineFilters.getIsWhole().toString())
          .append(" AND");
    }

    if (magazineFilters.getHasMonitoring() != null) {
      queryBuilder
          .append(" m.monitoring = ")
          .append(magazineFilters.getHasMonitoring().toString())
          .append(" AND");
    }

    if (magazineFilters.getHasAntiTheftDoors() != null) {
      queryBuilder
          .append(" m.antiTheftDoors = ")
          .append(magazineFilters.getHasAntiTheftDoors().toString())
          .append(" AND");
    }

    if (magazineFilters.getHasVentilation() != null) {
      queryBuilder
          .append(" m.ventilation = ")
          .append(magazineFilters.getHasVentilation().toString())
          .append(" AND");
    }

    if (magazineFilters.getHasSmokeDetectors() != null) {
      queryBuilder
          .append(" m.smokeDetectors = ")
          .append(magazineFilters.getHasSmokeDetectors().toString())
          .append(" AND");
    }

    if (magazineFilters.getIsSelfService() != null) {
      queryBuilder
          .append(" m.selfService = ")
          .append(magazineFilters.getIsSelfService().toString())
          .append(" AND");
    }

    if (magazineFilters.getMinFloor() != null) {
      queryBuilder
          .append(" m.floor >= ")
          .append(magazineFilters.getMinFloor().toString())
          .append(" AND");
    }

    if (magazineFilters.getMaxFloor() != null) {
      queryBuilder
          .append(" m.floor <= ")
          .append(magazineFilters.getMaxFloor().toString())
          .append(" AND");
    }

    if (magazineFilters.getMinDoorHeight() != null) {
      queryBuilder
          .append(" m.doorHeight >= ")
          .append(magazineFilters.getMinDoorHeight().toString())
          .append(" AND");
    }

    if (magazineFilters.getMinDoorWidth() != null) {
      queryBuilder
          .append(" m.doorWidth >= ")
          .append(magazineFilters.getMinDoorWidth().toString())
          .append(" AND");
    }

    if (magazineFilters.getMinHeight() != null) {
      queryBuilder
          .append(" m.height >= ")
          .append(magazineFilters.getMinHeight().toString())
          .append(" AND");
    }

    if (magazineFilters.getMinTemperature() != null) {
      queryBuilder
          .append(" m.minTemperature >= ")
          .append(magazineFilters.getMinTemperature().toString())
          .append(" AND");
    }

    if (magazineFilters.getMaxTemperature() != null) {
      queryBuilder
          .append(" m.maxTemperature <= ")
          .append(magazineFilters.getMaxTemperature().toString())
          .append(" AND");
    }

    if (magazineFilters.getHasElectricity() != null) {
      queryBuilder
          .append(" m.electricity = ")
          .append(magazineFilters.getHasElectricity().toString())
          .append(" AND");
    }

    if (magazineFilters.getHasParking() != null) {
      queryBuilder
          .append(" m.parking = ")
          .append(magazineFilters.getHasParking().toString())
          .append(" AND");
    }

    if (magazineFilters.getHasElevator() != null) {
      queryBuilder
          .append(" m.parking = ")
          .append(magazineFilters.getHasElevator().toString())
          .append(" AND");
    }

    if (magazineFilters.getHasVehicleManoeuvreArea() != null) {
      queryBuilder
          .append(" m.vehicleManoeuvreArea = ")
          .append(magazineFilters.getHasVehicleManoeuvreArea().toString())
          .append(" AND");
    }

    if (magazineFilters.getCanOwnerTransport() != null) {
      queryBuilder
          .append(" m.ownerTransport = ")
          .append(magazineFilters.getCanOwnerTransport().toString())
          .append(" AND");
    }

    if (magazineFilters.getCurrentlyReservedBy() != null) {
      if (!isUserIdentifier(magazineFilters.getCurrentlyReservedBy())) {
        throw new UserNotFoundException();
      }
      queryBuilder
          .append(" m.id in (SELECT r.magazine FROM Reservation r WHERE r.user = '")
          .append(magazineFilters.getCurrentlyReservedBy())
          .append("' AND r.endDate >= TO_DATE('")
          .append(today)
          .append("','yyyy-MM-dd')) AND");
    }

    if (magazineFilters.getHistoricallyReservedBy() != null) {
      if (!isUserIdentifier(magazineFilters.getHistoricallyReservedBy())) {
        throw new UserNotFoundException();
      }
      queryBuilder
          .append(" m.id in (SELECT r.magazine FROM Reservation r WHERE r.user = '")
          .append(magazineFilters.getHistoricallyReservedBy())
          .append("' AND r.endDate < TO_DATE('")
          .append(today)
          .append("','yyyy-MM-dd')) AND");
    }

    queryBuilder.setLength(queryBuilder.length() - 4);

    queryBuilder.append(" GROUP BY m.id HAVING 1 = 1 AND");

    if (magazineFilters.getAvailableOnly() != null) {
      BigDecimal neededArea = magazineFilters.getMinFreeArea() != null
          ? magazineFilters.getMinFreeArea()
          : BigDecimal.valueOf(1.0f);

      queryBuilder
          .append(" (SUM(r.areaInMeters) = NULL OR m.areaInMeters - SUM(r.areaInMeters) >= ")
          .append(neededArea.toString())
          .append(") AND");
    }

    queryBuilder.setLength(queryBuilder.length() - 4);

    if (sortBy != null && sortBy != Sort.unsorted()) {
      String sortByString = "m." + sortBy.toString().replace(":", "");
      queryBuilder
          .append(" ORDER BY ")
          .append(sortByString);
    }

    System.out.println(queryBuilder);

    return em.createQuery(queryBuilder.toString(), QueryResult.class);
  }

  // Temporary SQL Injection protection, will be changed when user accouts are added.
  private boolean isUserIdentifier(String identifier) {
    return !identifier.contains(" ") && identifier.contains("@");
  }
}
