package com.bankcore.customers.utils.mappers;

import com.bankcore.customers.dto.responses.LoginResponse;
import com.bankcore.customers.dto.responses.UserProfileResponse;
import com.bankcore.customers.dto.responses.RegisterResponse;
import com.bankcore.customers.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Mapper responsible for converting {@link UserEntity} objects
 * into response DTOs.
 * <p>
 * Implemented using MapStruct and integrated with Spring's
 * component model for dependency injection.
 * </p>
 *
 * <p>
 * This mapper ensures that only non-sensitive data is exposed
 * to the API layer.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a {@link UserEntity} into a {@link RegisterResponse}.
     * <p>
     * The {@code fullName} field is derived from the entity's
     * first and last name using a custom mapping method.
     * </p>
     *
     * @param user the persisted user entity
     * @return a response DTO containing non-sensitive user information
     */
    @Mapping(target = "fullName", source = ".", qualifiedByName = "fullName")
    RegisterResponse toRegisterResponse(UserEntity user);

    /**
     * Generates the full name of the user by concatenating
     * non-null first and last name values.
     *
     * @param user the user entity
     * @return a formatted full name string without null components
     */
    @Named("fullName")
    default String mapFullName(UserEntity user) {
        return Stream.of(user.getFirstName(), user.getLastName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    @Mapping(target = "token", source = "jwt")
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "customerId", source = "user.id")
    LoginResponse toLoginResponse(UserEntity user, String jwt, Long expiresIn);

    /**
     * Maps a {@link UserEntity} to a {@link UserProfileResponse} DTO.
     * <p>
     * This method transforms the internal database entity into a client-facing response.
     * It handles the conversion of specific fields, such as mapping the internal
     * creation date to the standardized response format.
     * </p>
     *
     * @param user The {@link UserEntity} containing the data from the persistence layer.
     * @return A {@link UserProfileResponse} populated with the user's information.
     * @see UserEntity
     * @see UserProfileResponse
     */
    @Mapping(source = "createdDate", target = "createdAt")
    UserProfileResponse toUserProfileResponse(UserEntity user);
}
