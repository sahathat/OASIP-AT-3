package sit.int221.at3.dtos.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import sit.int221.at3.dtos.category.CategoryDto;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
public class EventDto implements Serializable {

    private Integer id;

    public String bookingName;

    public String bookingEmail;

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

    private String eventNotes;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String eventFile;
}
