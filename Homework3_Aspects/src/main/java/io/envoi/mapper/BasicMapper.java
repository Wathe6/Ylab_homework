package io.envoi.mapper;

import org.mapstruct.MappingTarget;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper for creating objects from resultSet.
 * */
public interface BasicMapper<T, DTO> {
    T map(ResultSet rs) throws SQLException;

    DTO toDTO(T t);

    T toEntity(DTO dto);

    void updateFromDto(DTO dto, @MappingTarget T t);
}