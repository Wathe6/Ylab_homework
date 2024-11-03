package io.envoi.controller;

import io.envoi.model.Habit;
import io.envoi.model.dto.StatisticDTO;
import io.envoi.service.HabitService;
import io.envoi.service.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;
    private final HabitService habitService;

    @Operation(summary = "Check a habit by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habit checked successfully"),
            @ApiResponse(responseCode = "404", description = "Habit not found"),
            @ApiResponse(responseCode = "500", description = "Failed to check habit")
    })
    @GetMapping(value = "/{habitId}/check", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> check(
            @Parameter(name = "habitId", required = true) @RequestParam Long habitId) {

        Habit habit = habitService.get(habitId);

        if (habit == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Habit not found");
        }

        if (statisticService.check(habit.getId())) {
            return ResponseEntity.ok("Habit checked successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to check habit");
        }
    }

    @Operation(summary = "Get all statistic from account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habits doesn't exist"),
            @ApiResponse(responseCode = "500", description = "Error sorting habits"),
    })
    @GetMapping(value = "/{accountId}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(
            @Parameter(name = "accountId", required = true) @RequestParam Long accountId) {

        List<Habit> habits = habitService.getByAccountId(accountId);

        if(CollectionUtils.isEmpty(habits)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Habits doesn't exist");
        }

        Map<Long, List<StatisticDTO>> habitStatisticsMap = statisticService.getALl(habits);

        if (MapUtils.isEmpty(habitStatisticsMap)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sorting habits");
        }

        return ResponseEntity.ok(habitStatisticsMap);
    }
}