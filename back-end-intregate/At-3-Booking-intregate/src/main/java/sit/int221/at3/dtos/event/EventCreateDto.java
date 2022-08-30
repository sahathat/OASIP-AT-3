package sit.int221.at3.dtos.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
public class EventCreateDto implements Serializable{

    private Integer id;

    @Size(min = 1, max = 100, message = "name should have between 1 to 100 characters")
    private String bookingName;

    @Size(min = 1, max = 100, message = "email should have between 1 to 100 characters")
    @Email(message = "this is not email format")
    private String bookingEmail;

    @NotNull(message = "datetime should not null")
    @Future(message = "can schedule event by future date only.")
    private ZonedDateTime eventStartTime;

    @NotNull(message = "category id should not null")
    private Integer eventCategoryId;

    @JsonIgnore
    private Integer eventDuration;

    @Size(max=500, message = "note should have between 0 to 500 characters")
    private String eventNotes;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setBookingName(String bookingName) {
        this.bookingName = bookingName.trim();
    }

    public void setBookingEmail(String bookingEmail) {
        this.bookingEmail = bookingEmail.trim();
    }

    public void setEventStartTime(ZonedDateTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public void setEventCategoryId(Integer eventCategoryId) {
        this.eventCategoryId = eventCategoryId;
    }

    public void setEventDuration(Integer eventDuration) {
        this.eventDuration = eventDuration;
    }

    public void setEventNotes(String eventNotes) {
        this.eventNotes = eventNotes.trim();
    }
}
