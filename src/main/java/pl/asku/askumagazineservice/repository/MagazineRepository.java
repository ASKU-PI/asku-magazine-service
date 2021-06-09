package pl.asku.askumagazineservice.repository;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.asku.askumagazineservice.model.Magazine;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public interface MagazineRepository
        extends PagingAndSortingRepository<Magazine, Long>, QuerydslPredicateExecutor<Magazine> {
}
