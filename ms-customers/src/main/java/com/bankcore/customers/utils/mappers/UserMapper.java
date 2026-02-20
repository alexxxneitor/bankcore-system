package com.bankcore.customers.utils.mappers;

import com.bankcore.customers.dto.responses.RegisterResponse;
import com.bankcore.customers.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", source = ".", qualifiedByName = "fullName")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "atmPin", ignore = true)
    RegisterResponse toRegisterResponse(UserEntity user);

    @Named("fullName")
    default String mapFullName(UserEntity user) {
        return Stream.of(user.getFirstName(), user.getLastName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }
}
