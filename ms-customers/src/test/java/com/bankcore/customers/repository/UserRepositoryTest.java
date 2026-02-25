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
    void shouldFindByEmail() {
        UserEntity user1 = DataProvider.createMockUser();

       userRepository.save(user1);

       Optional<UserEntity> found = userRepository.findByEmail("juan@test.com");


        assertTrue(found.isPresent());
        assertEquals(found.get(), user1);

    }

}
