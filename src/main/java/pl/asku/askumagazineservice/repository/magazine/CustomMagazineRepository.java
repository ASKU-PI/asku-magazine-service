package pl.asku.askumagazineservice.repository.magazine;

import org.springframework.data.domain.PageRequest;
import pl.asku.askumagazineservice.exception.UserNotFoundException;
import pl.asku.askumagazineservice.model.magazine.search.MagazineFilters;
import pl.asku.askumagazineservice.model.magazine.search.MagazineSearchResult;

public interface CustomMagazineRepository {
  MagazineSearchResult search(MagazineFilters magazineFilters, PageRequest pageRequest)
      throws UserNotFoundException;
}
