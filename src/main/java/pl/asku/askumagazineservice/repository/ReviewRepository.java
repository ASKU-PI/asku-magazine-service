package pl.asku.askumagazineservice.repository;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.asku.askumagazineservice.model.Review;

public interface ReviewRepository extends CrudRepository<Review, Long> {
  Optional<Review> findByReservation_Id(Long id);

  Integer countByReservation_Magazine_Id(Long magazineId);

  @Query(
      "SELECT ROUND(AVG(rw.rating), 1) FROM Review rw JOIN Reservation rn ON rn.id = rw"
          + ".reservation.id JOIN Magazine m ON m.id = rn.magazine.id WHERE m.id = :magazineId")
  BigDecimal averageByReservation_Magazine_Id(Long magazineId);
}
