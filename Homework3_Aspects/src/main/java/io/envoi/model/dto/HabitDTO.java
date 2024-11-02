package io.envoi.model.dto;

import java.time.Period;

public record HabitDTO(
        Long id,
        Long accountId,
        String name,
        String description,
        Period period) {

}