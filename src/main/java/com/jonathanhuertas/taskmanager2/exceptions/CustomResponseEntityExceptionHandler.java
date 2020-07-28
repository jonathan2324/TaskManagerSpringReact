package com.jonathanhuertas.taskmanager2.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
/*
ResponseEntityExceptionHandler->A convenient base class for @ControllerAdvice classes that wish to provide centralized
exception handling across all @RequestMapping methods through @ExceptionHandler methods
 */
@RestController//we need to annotate with @RestController because we are sending a response in the case of exceptions
@ControllerAdvice//mechanism that helps break away from having exception handlers that are controller specific
//essentially sets up global exception handling for controllers

public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    //if there is a ProjectIdException thrown, this method will be called passing in the exception with the message
    //because we inherited from RuntimeException

    //then, the method will create a new object from our custom ProjectIdExceptionResponse and we pass the message from the RuntimeException
    //remember that in the ProjectIdExceptionResponse, we said that the field was named projectIdentifier just like in the Object Project
    //that way, any message from the error will find that field and be the value for the projectIdentifier
    //so the error will show up as { "projectIdentifier" : "Error message blah blah" }
    @ExceptionHandler
    public final ResponseEntity<Object> handleProjectIdException(ProjectIdException ex, WebRequest request){
        ProjectIdExceptionResponse exceptionResponse = new ProjectIdExceptionResponse(ex.getMessage());


        //return an object so no need to specify in ResponseEntity
        //because of the @RestController, this will automatically be turned into a JSON response to the web
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler
    public final ResponseEntity<Object> handleProjectNotFoundException(ProjectNotFoundException ex, WebRequest request){
        ProjectNotFoundExceptionResponse exceptionResponse = new ProjectNotFoundExceptionResponse(ex.getMessage());

        //return an object so no need to specify in ResponseEntity
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex, WebRequest request){
        UsernameAlreadyExistsResponse exceptionResponse = new UsernameAlreadyExistsResponse(ex.getMessage());

        //return an object so no need to specify in ResponseEntity
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

}
