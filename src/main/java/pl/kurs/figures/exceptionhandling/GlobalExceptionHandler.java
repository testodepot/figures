package pl.kurs.figures.exceptionhandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.kurs.figures.exception.BadEntityException;
import pl.kurs.figures.exception.EntityNotFoundException;
import pl.kurs.figures.exception.NoPermissionException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<List<ExceptionResponseMethodArgumentNotValid>> handleValidationException(MethodArgumentNotValidException e) {
        List<ExceptionResponseMethodArgumentNotValid> list = new ArrayList<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            Optional<Object> rejectedValue = Optional.ofNullable(error.getRejectedValue());
            String string = rejectedValue.map(Objects::toString).orElse("(empty)");
            ExceptionResponseMethodArgumentNotValid exceptionResponseArgumentNotValid = new ExceptionResponseMethodArgumentNotValid(error.getField(), string, error.getDefaultMessage(), LocalDateTime.now());
            list.add(exceptionResponseArgumentNotValid);
        }

        for (ObjectError error : e.getBindingResult().getGlobalErrors()) {
            Optional<Object> rejectedArguments = Optional.ofNullable(error.getArguments());
            String string = rejectedArguments.map(Objects::toString).orElse("(empty)");
            ExceptionResponseMethodArgumentNotValid exceptionResponseArgumentNotValid = new ExceptionResponseMethodArgumentNotValid(error.getObjectName(), string, error.getDefaultMessage(), LocalDateTime.now());
            list.add(exceptionResponseArgumentNotValid);
        }

        return new ResponseEntity<>(list, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(BadEntityException.class)
    public ResponseEntity<ExceptionResponseBadEntity> handleBadEntityException(BadEntityException e) {
        ExceptionResponseBadEntity response = new ExceptionResponseBadEntity(e.getMessage(), HttpStatus.BAD_REQUEST.toString(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponseNotFound> handleEntityNotFoundException(EntityNotFoundException e) {
        ExceptionResponseNotFound response = new ExceptionResponseNotFound(HttpStatus.NOT_FOUND.toString(), LocalDateTime.now(), e.getEntity(), e.getIdNotFound());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(NoPermissionException.class)
    public ResponseEntity<ExceptionResponseBadEntity> handleEntityNotFoundException(NoPermissionException e) {
        ExceptionResponseBadEntity response = new ExceptionResponseBadEntity(e.getMessage(), HttpStatus.FORBIDDEN.toString(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }



    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<List<ExceptionResponseHttpMessageNotReadable>> handleValidationException(HttpMessageNotReadableException e) {
        List<ExceptionResponseHttpMessageNotReadable> list = new ArrayList<>();
        ExceptionResponseHttpMessageNotReadable exceptionResponseArgumentNotValid = new ExceptionResponseHttpMessageNotReadable(e.getMessage(), LocalDateTime.now());
        list.add(exceptionResponseArgumentNotValid);
        return new ResponseEntity<>(list, HttpStatus.BAD_REQUEST);
    }








}
