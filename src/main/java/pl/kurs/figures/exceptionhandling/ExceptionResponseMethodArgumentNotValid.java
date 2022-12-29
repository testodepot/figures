package pl.kurs.figures.exceptionhandling;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class ExceptionResponseMethodArgumentNotValid {

    private String field;

    private String rejectedValue;

    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    public ExceptionResponseMethodArgumentNotValid(String field, String rejectedValue, String message, LocalDateTime timestamp) {
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
        this.timestamp = timestamp;
    }


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(String rejectedValue) {
        this.rejectedValue = rejectedValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
