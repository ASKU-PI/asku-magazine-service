package pl.asku.askumagazineservice.repository.magazine;

import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.asku.askumagazineservice.dto.magazine.MagazineBoundaryValuesDto;
import pl.asku.askumagazineservice.model.magazine.Magazine;

public interface MagazineRepository
    extends PagingAndSortingRepository<Magazine, Long>, JpaSpecificationExecutor<Magazine>,
    CustomMagazineRepository {

  List<Magazine> findAllByOwner_IdAndDeleted(String ownerId, Boolean deleted);

  @Query("SELECT m FROM Magazine m WHERE m.owner.id = :username AND m.endDate >= CURRENT_DATE AND"
      + " m.deleted <> true")
  List<Magazine> findAllActiveByOwner(String username);

  @Query("SELECT m FROM Magazine m WHERE m.owner.id = :username AND (m.endDate < CURRENT_DATE OR "
      + "m.deleted = true)")
  List<Magazine> findAllNotActiveByOwner(String username);

  List<Magazine> findAll();

  Magazine findFirstByOrderByAreaInMetersDesc();

  @Query("SELECT NEW pl.asku.askumagazineservice.dto.magazine.MagazineBoundaryValuesDto("
      + "MIN(m.areaInMeters), MAX(m.areaInMeters),"
      + "MIN(m.minTemperature), MAX(m.maxTemperature),"
      + "MIN(m.pricePerMeter), MAX(m.pricePerMeter),"
      + "MIN(m.doorHeight), MIN(m.doorWidth), MIN(m.height)) FROM Magazine m")
  MagazineBoundaryValuesDto getBoundaryValues();

  @Modifying
  @Query("UPDATE Magazine m SET m.deleted = true WHERE m.id = :id")
  void deleteById(Long id);
}
