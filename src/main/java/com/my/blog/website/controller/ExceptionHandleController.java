package com.my.blog.website.controller;

import com.my.blog.website.exception.TipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandleController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandleController.class);

    @ExceptionHandler(TipException.class)
    public String TipException(Exception e){
        logger.error("find exception:e={}",e.getMessage());
        e.printStackTrace();
        return "comm/error_500";
    }

    @ExceptionHandler(value = Exception.class)
    public String Exception(Exception e){
        logger.error("find exception:e={}",e.getMessage());
        e.printStackTrace();
        return "comm/error_404";
    }
}
