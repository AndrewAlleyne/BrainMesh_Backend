package com.alleynejr.brainmesh_backend.repository;


import com.alleynejr.brainmesh_backend.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String username);

    Optional<User> findByUsername(String username);

}
