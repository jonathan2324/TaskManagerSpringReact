package com.jonathanhuertas.taskmanager2.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;
/*
This service makes sure that the client is passing a valid Object when the @Valid is used.
This does not check for errors in regards to database persistence
so if we try to fetch a projectId that does not exist, this would not handle that error
One issue with this is the fact that the database will increment the id for rejected objects but will not persist the object
 */
@Service
public class MapValidationErrorService {

    public ResponseEntity<?> MapValidationService(BindingResult result){
        //Object FieldError - Encapsulates a field error, that is, a reason for rejecting a specific field value - has getters for getField and getDefaultMessage
        //BindingResult -> hasErrors checks for any global errors
        if(result.hasErrors()){
            //to get a key value pair for field errors and message
            Map<String, String> errorMap = new HashMap<>();

            //Binding Result has method getFieldErrors -> returns a List of FieldError instances
            //we loop through all the FieldError instances and say that each one is an error
            //for each FieldError, we use getField to Return the affected field of the object.
            for(FieldError error : result.getFieldErrors() ){
                //getting the field and the default message off the binding result
                //name of the field with error is the key (getField), default message is the value(getDefaultMessage)
                //this returns an array of objects each with a fieldName and the error message from the validation that failed
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
        }

        return null;
    }
}
