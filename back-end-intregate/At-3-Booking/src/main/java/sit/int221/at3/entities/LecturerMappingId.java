package sit.int221.at3.entities;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LecturerMappingId implements Serializable {
    private static final long serialVersionUID = -4169520223432839194L;
    @Column(name = "userId", nullable = false)
    private Integer userId;
    @Column(name = "categoryId", nullable = false)
    private Integer categoryId;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, categoryId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        LecturerMappingId entity = (LecturerMappingId) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.categoryId, entity.categoryId);
    }
}