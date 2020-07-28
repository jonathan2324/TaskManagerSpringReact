package com.jonathanhuertas.taskmanager2.services;

import com.jonathanhuertas.taskmanager2.domain.User;
import com.jonathanhuertas.taskmanager2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
UserDetailsService. The only method on this interface accepts a String-based username argument and returns a UserDetails

This is the most common approach to loading information for a user within Spring Security and you will see it used throughout the
framework whenever information on a user is required.

On successful authentication, UserDetails is used to build the Authentication object that is stored in the SecurityContextHolder

UserDetailsService is purely a DAO for user data and performs no other function other than to supply that data to other
components within the framework. In particular, it does not authenticate the user, which is done by the AuthenticationManager
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        //System.out.println(user instanceof UserDetails); -> true

        return user;
    }

    @Transactional
    public User loadUserById(Long id){
        User user = userRepository.getById(id);

        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
