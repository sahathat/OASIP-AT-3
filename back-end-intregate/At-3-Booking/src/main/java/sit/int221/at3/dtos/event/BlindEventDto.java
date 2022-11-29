package sit.int221.at3.dtos.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import sit.int221.at3.dtos.category.CategoryDto;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
public class BlindEventDto implements Serializable {

    private Integer id;

    @JsonIgnore
    private CategoryDto eventCategory;

    public String getCategoryName() {
        return eventCategory.getEventCategoryName();
    }

    private Integer eventDuration;

    private ZonedDateTime eventStartTime;

    public ZonedDateTime getEventStartTime(){
        return eventStartTime;
    }
}