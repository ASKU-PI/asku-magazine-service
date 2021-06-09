package pl.asku.askumagazineservice.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import pl.asku.askumagazineservice.model.Image;
import pl.asku.askumagazineservice.model.Magazine;

public interface ImageRepository extends CrudRepository<Image, Long> {
}
