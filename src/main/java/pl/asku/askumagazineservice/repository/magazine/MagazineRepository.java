package pl.asku.askumagazineservice.repository.magazine;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.asku.askumagazineservice.model.magazine.Magazine;

public interface MagazineRepository
    extends PagingAndSortingRepository<Magazine, Long>, JpaSpecificationExecutor<Magazine>,
    CustomMagazineRepository {

  List<Magazine> findAllByOwner_IdAndDeleted(String ownerId, Boolean deleted);

  @Query("SELECT m FROM Magazine m WHERE m.owner.id = :username AND m.endDate >= CURRENT_DATE")
  List<Magazine> findAllActiveByOwner(String username);

  List<Magazine> findAll();

  Magazine findFirstByOrderByAreaInMetersDesc();

  @Modifying
  @Query("UPDATE Magazine m SET m.deleted = true WHERE m.id = :id")
  void deleteById(Long id);
}
