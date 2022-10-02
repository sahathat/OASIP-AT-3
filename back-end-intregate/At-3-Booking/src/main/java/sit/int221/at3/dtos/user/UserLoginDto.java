package sit.int221.at3.dtos.user;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
public class UserLoginDto implements Serializable {

    @Size(min = 1, max = 50, message = "email should have between 1 to 50 characters")
    @Email(message = "this is not email format")
    private String email;

    @NotNull(message = "password should not null")
    @Size(min = 8, max = 14, message = "password should have between 8 to 14 characters")
    private String password;

    public void setEmail(String email){
        this.email = email.trim();
    }

    public void setPassword(String password){
        this.password = password;
    }
}
