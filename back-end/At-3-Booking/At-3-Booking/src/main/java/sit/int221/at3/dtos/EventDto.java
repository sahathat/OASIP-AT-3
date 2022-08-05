package sit.int221.at3.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
}
