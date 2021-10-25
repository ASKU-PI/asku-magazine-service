package pl.asku.askumagazineservice.repository.magazine;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.asku.askumagazineservice.model.magazine.Magazine;

import java.util.List;

public interface MagazineRepository
        extends PagingAndSortingRepository<Magazine, Long>, JpaSpecificationExecutor<Magazine>,
        CustomMagazineRepository {

    List<Magazine> findAllByOwner(String username, PageRequest pageRequest);

    List<Magazine> findAll();

    Magazine findFirstByOrderByAreaInMetersDesc();
}
