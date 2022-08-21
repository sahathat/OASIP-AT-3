package sit.int221.at3.dtos.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import sit.int221.at3.entities.Role;

import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class UserModifyDto implements Serializable {
    @JsonIgnore
    private Integer id;

    @Size(min = 1, max = 100, message = "name should have between 1 to 100 characters")
    private String name;

    @Size(min = 1, max = 50, message = "email should have between 1 to 50 characters")
    @Email(message = "this is not email format")
    private String email;

    private Role role;
}
