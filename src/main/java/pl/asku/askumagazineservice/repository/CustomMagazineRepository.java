package pl.asku.askumagazineservice.repository;

import org.springframework.data.domain.PageRequest;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.Magazine;
import pl.asku.askumagazineservice.model.search.MagazineFilters;

import java.util.List;

public interface CustomMagazineRepository {
    List<Magazine> search(MagazineFilters magazineFilters, PageRequest pageRequest) throws UserNotFoundException;
}
