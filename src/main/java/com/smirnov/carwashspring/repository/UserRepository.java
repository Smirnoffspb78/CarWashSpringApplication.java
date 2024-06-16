package com.smirnov.carwashspring.repository;

import com.smirnov.carwashspring.entity.users.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByIdAndRole(Integer id, User.Role role);
}
