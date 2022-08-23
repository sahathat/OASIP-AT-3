package sit.int221.at3.errors;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ErrorDetailDto {
    String message;
    ZonedDateTime dateTime;

    public ErrorDetailDto(String message) {
        this.dateTime = ZonedDateTime.now();
        this.message = message;
    }
}
