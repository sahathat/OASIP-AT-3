package sit.int221.at3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sit.int221.at3.entities.Event;
import sit.int221.at3.entities.Role;
import sit.int221.at3.entities.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
}