package pl.asku.askumagazineservice.repository;

import org.springframework.data.repository.CrudRepository;
import pl.asku.askumagazineservice.model.User;

public interface UserRepository extends CrudRepository<User, String> {
}
