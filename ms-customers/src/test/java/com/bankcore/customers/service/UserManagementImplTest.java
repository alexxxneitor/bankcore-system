package com.bankcore.customers.service;

import com.bankcore.customers.dto.responses.UserProfileResponse;
import com.bankcore.customers.exception.UserProfileNotFoundException;
import com.bankcore.customers.model.UserEntity;
import com.bankcore.customers.repository.UserRepository;
import com.bankcore.customers.utils.mappers.UserMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserManagementImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserManagementImpl service;

    @Test
    void shouldReturnProfileWhenUserExists() {
        String email = "dev@mail.com";

        UserEntity user = new UserEntity();
        UserProfileResponse response = UserProfileResponse.builder().build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toUserProfileResponse(user)).thenReturn(response);

        UserProfileResponse result = service.getCurrentUserProfile(email);

        assertEquals(response, result);
        verify(userRepository).findByEmail(email);
        verify(userMapper).toUserProfileResponse(user);

    }

    @Test
    void shouldThrowWhenEmailIsNull() {
        assertThrows(IllegalArgumentException.class, () -> service.getCurrentUserProfile(null));
    }

    @Test
    void shouldThrowWhenEmailIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> service.getCurrentUserProfile(""));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("x")).thenReturn(Optional.empty());
        assertThrows(UserProfileNotFoundException.class, () -> service.getCurrentUserProfile("x"));
    }

}
