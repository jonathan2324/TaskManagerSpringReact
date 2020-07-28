package com.jonathanhuertas.taskmanager2.repositories;

import com.jonathanhuertas.taskmanager2.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

    User getById(Long id);

    //could have also done
    //Optional<User> findById(Long id);



}
