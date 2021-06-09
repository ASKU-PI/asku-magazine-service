package pl.asku.askumagazineservice.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.asku.askumagazineservice.model.Magazine;

public interface MagazineRepository
        extends PagingAndSortingRepository<Magazine, Long>, JpaSpecificationExecutor<Magazine> {
}
