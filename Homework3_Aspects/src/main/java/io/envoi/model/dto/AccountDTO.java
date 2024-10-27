package io.envoi.model.dto;

import io.envoi.enums.Roles;

public record AccountDTO (
        Long id,
        String email,
        String name,
        Roles role) {

}