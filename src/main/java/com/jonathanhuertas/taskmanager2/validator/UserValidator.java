package com.jonathanhuertas.taskmanager2.validator;

import com.jonathanhuertas.taskmanager2.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/*
implementing the Validator allows us to add additional validation to certain fields on a class
 */

@Component
public class UserValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass); //supporting our User class
    }


    //We pass in the BindingResult along with the User from the register on UserController
    //BindingResult is a class that extends Errors and User is an Object. That is why we can pass both into this method

    @Override
    public void validate(Object object, Errors errors) {
        User user = (User) object;

        if(user.getPassword().length() < 6){
            errors.rejectValue("password", "Length", "Password must be at least 6 characters"); //password has to match the field on the User
        }

        if(!user.getPassword().equals(user.getConfirmPassword())){
            errors.rejectValue("confirmPassword", "Match", "Password must match");
        }
    }
}
