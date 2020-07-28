package com.jonathanhuertas.taskmanager2.services;

import com.jonathanhuertas.taskmanager2.domain.User;
import com.jonathanhuertas.taskmanager2.exceptions.UsernameAlreadyExistsException;
import com.jonathanhuertas.taskmanager2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public User saveUser(User newUser){

        try{
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));


            //Username has to be unique -> need custom exception
            newUser.setUsername(newUser.getUsername());//-> this is where the exception would be thrown for duplicate users


            //Make sure that password and confirm password match
            //we dont persist or show confirmPassword
            newUser.setConfirmPassword("");//-> set the confirmPassword to nothing so it does not show in the response JSON

            return userRepository.save(newUser);

        } catch(Exception e) {
            throw new UsernameAlreadyExistsException("Username " + newUser.getUsername() + " already exists");
        }

    }
}
