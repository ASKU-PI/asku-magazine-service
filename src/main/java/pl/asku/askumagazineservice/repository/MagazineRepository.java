package pl.asku.askumagazineservice.repository;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.asku.askumagazineservice.model.Magazine;

import java.util.List;

public interface MagazineRepository
        extends PagingAndSortingRepository<Magazine, Long>, QuerydslPredicateExecutor<Magazine> {
}
