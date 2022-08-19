package sit.int221.at3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.at3.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}