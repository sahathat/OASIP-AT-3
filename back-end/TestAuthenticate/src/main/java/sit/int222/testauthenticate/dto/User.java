package sit.int222.testauthenticate.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class User implements Serializable {
    private static final long serialVersionUID = 5926468583005150707L;

    public User() {

    }

    private String email;
    private String password;
}
