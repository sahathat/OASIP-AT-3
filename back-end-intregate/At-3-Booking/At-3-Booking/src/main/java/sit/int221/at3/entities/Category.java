package sit.int221.at3.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "Category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventCategoryId", nullable = false)
    private Integer id;

    @Column(name = "eventCategoryName", nullable = false, length = 100)
    private String eventCategoryName;

    @Column(name = "eventCategoryDescription", length = 500)
    private String eventCategoryDescription;

    @Column(name = "eventCategoryDuration", nullable = false)
    private Integer eventCategoryDuration;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private Set<LecturerMapping> lecturerMappings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "eventCategory")
    private Set<Event> events = new LinkedHashSet<>();

    @JsonIgnore
    public Set<Event> getEventList() {
        return events;
    }

    public void setEventList(Set<Event> events) {
        this.events = events;
    }

    public Set<LecturerMapping> getLecturerMappings() {
        return lecturerMappings;
    }

    public void setLecturerMappings(Set<LecturerMapping> lecturerMappings) {
        this.lecturerMappings = lecturerMappings;
    }

    public Integer getEventCategoryDuration() {
        return eventCategoryDuration;
    }

    public void setEventCategoryDuration(Integer eventCategoryDuration) {
        this.eventCategoryDuration = eventCategoryDuration;
    }

    public String getEventCategoryDescription() {
        return eventCategoryDescription;
    }

    public void setEventCategoryDescription(String eventCategoryDescription) {
        this.eventCategoryDescription = eventCategoryDescription;
    }

    public String getEventCategoryName() {
        return eventCategoryName;
    }

    public void setEventCategoryName(String eventCategoryName) {
        this.eventCategoryName = eventCategoryName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}