package pl.asku.askumagazineservice.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.asku.askumagazineservice.model.Magazine;

public interface MagazineRepository
        extends PagingAndSortingRepository<Magazine, Long>, QuerydslPredicateExecutor<Magazine> {
}
