package sit.int221.at3.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CategoryDto implements Serializable {
    private Integer id;
    private String eventCategoryName;
    private String eventCategoryDescription;
    private Integer eventCategoryDuration;
}
