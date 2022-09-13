package org.hatama.tasker.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class TaskerException extends RuntimeException {

    private HttpStatus code;

    public TaskerException(String message, HttpStatus code) {
        super(message);
        this.code = code;
    }
}
