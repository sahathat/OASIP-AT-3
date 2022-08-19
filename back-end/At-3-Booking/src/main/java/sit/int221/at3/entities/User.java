package sit.int221.at3.entities;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "role", nullable = false, length = 10)
    private String role;

    @Column(name = "createdOn", nullable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updateOn", nullable = false)
    private ZonedDateTime updateOn;

    public ZonedDateTime getUpdateOn() {
        return updateOn;
    }

    public void setUpdateOn(ZonedDateTime updateOn) {
        this.updateOn = updateOn;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}