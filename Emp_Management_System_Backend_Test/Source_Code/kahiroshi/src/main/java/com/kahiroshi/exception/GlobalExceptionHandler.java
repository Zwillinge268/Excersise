package com.kahiroshi.exception;

import com.kahiroshi.object.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Handle all exception and return error message
    //Object will turn to json automatically as controller
    @ExceptionHandler(Exception.class)
    public Result exception(Exception e) {
        return Result.error("Action Failed");
    }
}
