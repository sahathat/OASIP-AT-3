package sit.int221.at3.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "Lecturer_Mapping")
public class LecturerMapping {
    @EmbeddedId
    private LecturerMappingId id;

    @MapsId("userId")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @MapsId("categoryId")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    @Column(name = "mappingId", nullable = false)
    private Integer mappingId;

    public Integer getMappingId() {
        return mappingId;
    }

    public void setMappingId(Integer mappingId) {
        this.mappingId = mappingId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LecturerMappingId getId() {
        return id;
    }

    public void setId(LecturerMappingId id) {
        this.id = id;
    }
}