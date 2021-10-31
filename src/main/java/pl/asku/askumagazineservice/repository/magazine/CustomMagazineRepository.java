package pl.asku.askumagazineservice.repository.magazine;

import org.springframework.data.domain.PageRequest;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.magazine.Magazine;
import pl.asku.askumagazineservice.model.magazine.search.MagazineFilters;
import pl.asku.askumagazineservice.model.magazine.search.SearchResult;

import java.util.List;

public interface CustomMagazineRepository {
    SearchResult search(MagazineFilters magazineFilters, PageRequest pageRequest) throws UserNotFoundException;
}
