package pl.asku.askumagazineservice.repository;

import org.springframework.data.repository.CrudRepository;
import pl.asku.askumagazineservice.model.Review;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    Review findByReservation_Id(Long id);
}
