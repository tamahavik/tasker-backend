package org.project.tasker.exception;

import org.project.tasker.model.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(TaskerException.class)
    protected ResponseEntity<Object> handleBadRequestException(TaskerException e, WebRequest request) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(e.getCode().value())
                .message(e.getMessage())
                .path(request.getDescription(false).split("=")[1])
                .build();
        return ResponseEntity.status(e.getCode())
                .body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, WebRequest request) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(METHOD_NOT_ALLOWED.value())
                .message(e.getMessage())
                .path(request.getDescription(false).split("=")[1])
                .build();
        return ResponseEntity.status(METHOD_NOT_ALLOWED)
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request) {
        BindingResult result = e.getBindingResult();
        List<String> globalErrors = result.getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(toList());
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(BAD_REQUEST.value())
                .message(globalErrors)
                .path(request.getDescription(false).split("=")[1])
                .build();
        return ResponseEntity.status(BAD_REQUEST.value())
                .body(body);
    }

}
