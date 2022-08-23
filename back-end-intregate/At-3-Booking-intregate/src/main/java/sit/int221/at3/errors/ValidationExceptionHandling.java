package sit.int221.at3.errors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ValidationExceptionHandling extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Object> handleExceptions(Exception ex) {
        ErrorDetailDto errorDetails = new ErrorDetailDto(ex.getMessage());
        return new ResponseEntity<>(errorDetails,HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<ErrorMessageDto> validationErrorDetails = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorMessageDto(error.getField(),error.getDefaultMessage(),error.getRejectedValue().toString()))
                .collect(Collectors.toList());

        ErrorResponse response = new ErrorResponse(status.name(), status.value(),validationErrorDetails);
        return new ResponseEntity<>(response,status);
    }
}
