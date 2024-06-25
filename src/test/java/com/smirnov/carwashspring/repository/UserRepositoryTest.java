package com.smirnov.carwashspring.repository;

import com.smirnov.carwashspring.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void findByIdAndRoleAndDeletedIsFalse() {
    }

    @Test
    void findByIdAndRoleIsOperatorOrRoleAndDeletedIsFalseIsAdmin_Operator() {
        assertEquals(userRepository.findByIdAndRoleIsOperatorOrRoleIsAdmin(5).get()
                ,userRepository.findById(5).get());
    }

    @Test
    void findByIdAndRoleIsOperatorOrRoleAndDeletedIsFalseIsAdmin_Admin() {
        assertEquals(userRepository.findByIdAndRoleIsOperatorOrRoleIsAdmin(6).get()
                ,userRepository.findById(6).get());
    }
    @Test
    void findByIdAndRoleIsOperatorOrRoleAndDeletedIsFalseIsAdmin_Throw() {
        assertThrows(UserNotFoundException.class, () -> userRepository.findByIdAndRoleIsOperatorOrRoleIsAdmin(4).orElseThrow(()-> new UserNotFoundException()));
    }

    @Test
    void findByLoginAndDeletedIsFalse() {
    }

    @Test
    void findByIdAndDeletedIsFalse() {
    }

    @Test
    void existsByIdAndRole() {
    }
}