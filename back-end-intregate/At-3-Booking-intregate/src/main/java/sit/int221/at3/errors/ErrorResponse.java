package sit.int221.at3.errors;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    private String error;
    private int code;
    ZonedDateTime dateTime;

    private List<ErrorMessageDto> errors;


    public ErrorResponse() {
    }

    public ErrorResponse(String error, int code, List<ErrorMessageDto> errors) {
        this.error = error;
        this.code = code;
        this.dateTime = ZonedDateTime.now();
        this.errors = errors;
    }
}