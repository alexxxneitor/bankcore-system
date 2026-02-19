package com.bankcore.customers.utils.mappers;

import com.bankcore.customers.dto.responses.RegisterResponses;
import com.bankcore.customers.dto.responses.UserProfileResponse;
import com.bankcore.customers.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", source = ".", qualifiedByName = "fullName")
    RegisterResponses toRegisterResponse(UserEntity user);

    @Named("fullName")
    default String mapFullName(UserEntity user){
        return user.getFirstName() + " " + user.getLastName();
    }

    @Mapping(source = "createdDate", target = "createdAt")
    UserProfileResponse toUserProfileResponse(UserEntity user);
}
