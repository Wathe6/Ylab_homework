package io.envoi.model.dto;

import java.time.LocalDate;

public record StatisticDTO(
        LocalDate date,
        Boolean status,
        Integer streak,
        Double percentile) {

}