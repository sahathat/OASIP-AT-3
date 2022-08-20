package sit.int221.at3.dtos.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.Constraint;
import javax.validation.constraints.*;
import java.time.ZonedDateTime;

@Getter
@Setter
public class CategoryUpdateDto {
    @JsonIgnore
    private Integer id;

    @Size(min=1,max = 100,message = "name should have between 1 to 100 characters")
    private String eventCategoryName;

    @Size(max = 500,message = "description should have between 0 to 500 characters")
    private String eventCategoryDescription;

    @Min(value = 1,message = "time duration can set at least 1")
    @Max(value = 480,message = "time duration can set at most 480")
    private Integer eventCategoryDuration;
}
