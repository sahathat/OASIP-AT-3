package sit.int221.at3.errors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessageDto {
    private String field;
    private String message;
    private Object rejectedValue;

    public ErrorMessageDto() {
    }

    public ErrorMessageDto(String field, String message, Object rejectedValue) {
        this.field = field;
        this.message = message;
        this.rejectedValue = rejectedValue;
    }
}
