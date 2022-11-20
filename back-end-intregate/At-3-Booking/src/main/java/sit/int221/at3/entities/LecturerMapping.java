package sit.int221.at3.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "Lecturer_Mapping")
public class LecturerMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mappingId", nullable = false)
    private Integer mappingId;

    @Column(name = "lecturerEmail", length = 50, nullable = false)
    private String email;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}