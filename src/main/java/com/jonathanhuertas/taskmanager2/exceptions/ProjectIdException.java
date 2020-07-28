package com.jonathanhuertas.taskmanager2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
This is the error we throw in relation to any problems regarding fetching or querying the database for a project by Id
for example, if a user attempts to save a new project with a projectIdentifier that is already existing in the database
 */

@ResponseStatus(HttpStatus.BAD_REQUEST) //sets the status if there is an exception
public class ProjectIdException extends RuntimeException {

    public ProjectIdException(String message) {
        super(message);
    }
}
