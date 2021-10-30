package pl.asku.askumagazineservice.repository;

import org.springframework.data.repository.CrudRepository;
import pl.asku.askumagazineservice.model.Review;

import java.util.Optional;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    Optional<Review> findByReservation_Id(Long id);
}
