package io.envoi.controller;

import io.envoi.model.Habit;
import io.envoi.model.dto.StatisticDTO;
import io.envoi.service.HabitService;
import io.envoi.service.StatisticService;
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

    @GetMapping(value = "/{habitId}/check", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> check(@RequestParam Long habitId) {
        Habit habit = habitService.get(habitId);

        if (habit == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Habit doesn't exist");
        }

        if (statisticService.check(habit.getId())) {
            return ResponseEntity.ok("Habit checked successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to check habit");
        }
    }

    @GetMapping(value = "/{accountId}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(@RequestParam Long accountId) {
        List<Habit> habits = habitService.getByAccountId(accountId);

        if(CollectionUtils.isEmpty(habits)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Habits doesn't exist");
        }

        Map<Long, List<StatisticDTO>> habitStatisticsMap = statisticService.getALl(habits);

        if (MapUtils.isEmpty(habitStatisticsMap)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error sorting habits");
        }

        return ResponseEntity.ok(habitStatisticsMap);
    }
}