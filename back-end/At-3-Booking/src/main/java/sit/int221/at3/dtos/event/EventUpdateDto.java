package sit.int221.at3.dtos.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
public class EventUpdateDto implements Serializable {
    @JsonIgnore
    private Integer id;

    @NotNull(message = "datetime should not null")
    @Future(message = "can reschedule event by future date only.")
    private ZonedDateTime eventStartTime;

    @Size(max=500, message = "note should have between 0 to 500 characters")
    private String eventNotes;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEventStartTime(ZonedDateTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public void setEventNotes(String eventNotes) {
        this.eventNotes = eventNotes == null ? null : eventNotes.trim();
    }
}
