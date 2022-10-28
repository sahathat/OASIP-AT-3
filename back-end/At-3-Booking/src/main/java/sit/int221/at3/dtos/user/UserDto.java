package sit.int221.at3.dtos.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
public class UserDto implements Serializable {
    private Integer id;
    private String name;
    private String email;
    private String role;
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;
}
