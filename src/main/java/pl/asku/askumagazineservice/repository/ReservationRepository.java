package pl.asku.askumagazineservice.repository;

import org.springframework.data.repository.CrudRepository;
import pl.asku.askumagazineservice.model.Reservation;

import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    List<Reservation> findByMagazine_Id(Long id);
}
