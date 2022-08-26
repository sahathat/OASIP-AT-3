package sit.int221.at3.dtos.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserLoginDto implements Serializable {

    private String email;

    private String password;
}
