package com.catprogrammer.jira.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.google.gson.JsonObject;
/*
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { MethodArgumentTypeMismatchException.class })
    protected ResponseEntity<String> handleMethodArgumentTypeMismatchException(RuntimeException ex,
            WebRequest request) {
        JsonObject json = new JsonObject();
        json.addProperty("valid", "false");
        json.addProperty("error", ex.getMessage());
        return new ResponseEntity<String>(json.toString(), HttpStatus.BAD_REQUEST);
    }
}
*/