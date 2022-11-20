package sit.int221.at3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.at3.entities.Category;
import sit.int221.at3.entities.LecturerMapping;

import java.util.List;

public interface LecturerMappingRepository extends JpaRepository<LecturerMapping, Integer> {
    @Query(nativeQuery = true, value = "select * from Lecturer_Mapping lm where lm.categoryId = :id")
    List<LecturerMapping> getLecturerMappingByCategory(@Param("id") Integer id);

    LecturerMapping findByCategoryAndEmail(Category category, String email);

    @Query(nativeQuery = true, value = "select * from Lecturer_Mapping lm where lm.lecturerEmail = :email")
    List<LecturerMapping> getLecturerMappingByEmail(@Param("email") String email);
}