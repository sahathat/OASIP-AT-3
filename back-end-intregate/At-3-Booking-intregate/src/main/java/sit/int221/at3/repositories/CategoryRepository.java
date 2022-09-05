package sit.int221.at3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.int221.at3.entities.Category;
import sit.int221.at3.entities.Event;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}