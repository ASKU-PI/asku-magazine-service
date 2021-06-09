package pl.asku.askumagazineservice.repository;

import org.springframework.data.repository.CrudRepository;
import pl.asku.askumagazineservice.model.Image;

public interface ImageRepository extends CrudRepository<Image, Long> {
}
