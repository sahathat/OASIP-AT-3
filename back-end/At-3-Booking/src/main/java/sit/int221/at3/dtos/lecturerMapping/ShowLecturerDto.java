package sit.int221.at3.dtos.lecturerMapping;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ShowLecturerDto implements Serializable {
    private Integer id;
    private String name;
    private String email;
}
