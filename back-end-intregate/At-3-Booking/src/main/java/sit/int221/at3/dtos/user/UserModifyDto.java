package sit.int221.at3.dtos.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import sit.int221.at3.entities.Role;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
public class UserModifyDto implements Serializable {
    @JsonIgnore
    private Integer id;

    @Size(min = 1, max = 100, message = "name should have between 1 to 100 characters")
    private String name;

    @Size(min = 1, max = 50, message = "email should have between 1 to 50 characters")
    @Email(message = "this is not email format")
    private String email;

    @NotNull(message = "password should not null")
    @Size(min = 8, max = 14, message = "password should have between 8 to 14 characters")
    private String password;

    private Role role;

    public void setId(Integer id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name.trim();
    }

    public void setEmail(String email){
        this.email = email.trim();
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setRole(Role role){
        this.role = role;
    }
}
