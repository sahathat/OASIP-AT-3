package sit.int221.at3.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
public class EventUpdateDto implements Serializable {
    @JsonIgnore
    private Integer id;

    @NotNull()
    @Future(message = "can reschedule event by future date only.")
    private ZonedDateTime eventStartTime;

    @Size(max=500, message = "note should have between 0 to 500 characters")
    private String eventNotes;
}
