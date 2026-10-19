package br.com.creche.api.infra;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler { @ExceptionHandler(value = {Exception.class})

    public ResponseEntity<String> handleException(Exception ex){
        return ResponseEntity.status(400).body(ex.getMessage());
    }
}
