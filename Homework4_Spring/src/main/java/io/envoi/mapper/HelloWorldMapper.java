package io.envoi.mapper;

import io.envoi.model.HelloWorld;
import io.envoi.model.dto.HelloWorldDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HelloWorldMapper {
    @Mapping(source = "message", target = "message")
    HelloWorldDTO modelToDTO(HelloWorld model);
    @Mapping(source = "message", target = "message")
    HelloWorld dtoToModel(HelloWorldDTO dto);
}
