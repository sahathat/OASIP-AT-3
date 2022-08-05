package sit.int221.at3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.at3.entities.Event;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query(nativeQuery = true, value = "select * from Event where eventStartTime > now()")
    List<Event> getEventUpcoming();

    @Query(nativeQuery = true, value = "select * from Event where eventStartTime < now()")
    List<Event> getEventPast();

    @Query(nativeQuery = true, value = "select * from Event e where DATE_FORMAT(e.eventStartTime,'%Y-%m-%d') = :date")
    List<Event> getEventListDay(@Param("date") LocalDate date);

    @Query(nativeQuery = true, value = "select * from Event e where DATE_FORMAT(e.eventStartTime,'%Y-%m-%d') = curdate()")
    List<Event> getEventListToday();
}