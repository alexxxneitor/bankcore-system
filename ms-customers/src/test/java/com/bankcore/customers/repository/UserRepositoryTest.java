package com.bankcore.customers.repository;

import com.bankcore.customers.AbstractIntegrationTest;
import com.bankcore.customers.DataProvider;
import com.bankcore.customers.model.UserEntity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
class UserRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void shouldFindById() {
        UserEntity user1 = DataProvider.createMockUser();

        UserEntity savedUser = userRepository.save(user1);

        Optional<UserEntity> found = userRepository.findById(savedUser.getId());

        assertTrue(found.isPresent());
        assertEquals(found.get(), savedUser);

    }

    @Test
    void shouldFindByEmail() {
        UserEntity user1 = DataProvider.createMockUser();

        UserEntity savedUser = userRepository.save(user1);

        Optional<UserEntity> found = userRepository.findByEmail(savedUser.getEmail());

        assertTrue(found.isPresent());
        assertEquals(found.get(), savedUser);
    }


}
