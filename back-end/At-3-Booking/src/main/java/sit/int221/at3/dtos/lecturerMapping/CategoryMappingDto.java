package sit.int221.at3.dtos.lecturerMapping;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class CategoryMappingDto {
    @Email(message = "this is not email format")
    private String email;

    private Integer categoryId;
}
