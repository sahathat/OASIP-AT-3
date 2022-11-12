package sit.int221.at3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.at3.entities.ConfirmUser;
import sit.int221.at3.entities.User;

public interface ConfirmUserRepository extends JpaRepository<ConfirmUser, Integer> {
    public ConfirmUser findByToken(String token);

    public ConfirmUser findByUser(User user);
}