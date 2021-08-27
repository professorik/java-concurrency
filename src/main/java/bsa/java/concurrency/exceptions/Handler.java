package bsa.java.concurrency.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * @author professorik
 * @created 28/06/2021 - 17:18
 * @project concurrency
 */
@Slf4j
@ControllerAdvice
public class Handler {

    @ExceptionHandler(HashingException.class)
    public ResponseEntity<?> handleCalculatingHashException(HashingException ex) {
        log.error("HashingException: " + ex.getMessage());
        return new ResponseEntity<>(Map.of("reason", "Some error while hashing"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        log.error("NullPointerException: " + ex.getMessage());
        return new ResponseEntity<>(Map.of("reason", "The id is incorrect"), HttpStatus.FORBIDDEN);
    }
}
