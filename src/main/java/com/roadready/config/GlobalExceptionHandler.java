package com.roadready.config;

import com.roadready.exception.FileInvalidExtensionException;
import com.roadready.exception.FileNotFoundException;
import com.roadready.exception.ResourceNotFoundException;
import com.roadready.utility.ResponseUtility;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private ResponseUtility responseUtility;

    //handler method
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseUtility> handleResourceNotFoundException(
            ResourceNotFoundException e
    ){
        logger.error("Error in fetching user details {}", e.getMessage());
        responseUtility.setMessage(e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(responseUtility);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e , Principal principal
    ){
        logger.warn("Validation failed for user {} ", principal.getName() );
        BindingResult bindingResult =  e.getBindingResult();
        List<FieldError> errors =  bindingResult.getFieldErrors();
        Map<String, String> map = new HashMap<>();
        for (FieldError error : errors) {
            map.put(error.getField(), error.getDefaultMessage());
            logger.error("Field {} - message: {}", error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity
                .badRequest()
                .body(map);
    }

    // FileNotFoundException
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ResponseUtility> handleFileNotFoundException(
            FileNotFoundException e
    ){
        responseUtility.setMessage(e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(responseUtility);
    }

    // IOException
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ResponseUtility> handleIOException(
            IOException e
    ){
        responseUtility.setMessage(e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(responseUtility);
    }

    //FileInvalidExtensionException
    @ExceptionHandler(FileInvalidExtensionException.class)
    public ResponseEntity<ResponseUtility> handleFileInvalidExtensionException(
            FileInvalidExtensionException e
    ){
        responseUtility.setMessage(e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(responseUtility);
    }

    //UserAlreadyExistsException
    @ExceptionHandler(com.roadready.exception.UserAlreadyExistsException.class)
    public ResponseEntity<ResponseUtility> handleUserAlreadyExistsException(
            com.roadready.exception.UserAlreadyExistsException e
    ){
        responseUtility.setMessage(e.getMessage());
        return ResponseEntity
                .status(org.springframework.http.HttpStatus.CONFLICT)
                .body(responseUtility);
    }
}