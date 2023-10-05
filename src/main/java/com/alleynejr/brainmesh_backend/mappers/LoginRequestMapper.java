package com.alleynejr.brainmesh_backend.mappers;

import com.alleynejr.brainmesh_backend.dto.LoginRequestDTO;
import com.alleynejr.brainmesh_backend.model.LoginRequest;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LoginRequestMapper {

    @Mapping(target = "password", ignore = true)
    LoginRequestDTO loginRequestToDTO(LoginRequest loginRequest);

    @Mapping(source = "password", target = "password")
    LoginRequest fromDTO(LoginRequestDTO loginRequestDTO);

    //This method is executed after the forward mapping is done. From (entity to entityDTO)
    @BeforeMapping
    default void mapPassword(LoginRequestDTO loginRequestDTO, @MappingTarget LoginRequest loginRequest) {
        loginRequest.setPassword(loginRequestDTO.getPassword());
    }

}
