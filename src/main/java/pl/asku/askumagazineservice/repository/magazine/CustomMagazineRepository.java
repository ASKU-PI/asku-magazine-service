package pl.asku.askumagazineservice.repository.magazine;

import org.springframework.data.domain.PageRequest;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.magazine.search.MagazineFilters;

import java.util.List;

public interface CustomMagazineRepository {
    List<Magazine> search(MagazineFilters magazineFilters, PageRequest pageRequest) throws UserNotFoundException;
}
