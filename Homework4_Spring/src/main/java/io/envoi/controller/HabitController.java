package io.envoi.controller;

import io.envoi.mapper.HabitMapper;
import io.envoi.model.Habit;
import io.envoi.model.Statistic;
import io.envoi.model.dto.HabitDTO;
import io.envoi.service.HabitService;
import io.envoi.service.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {
    private final HabitService habitService;
    private final StatisticService statisticService;
    private final HabitMapper habitMapper = HabitMapper.INSTANCE;

    /**
     * Get the list of checked and unchecked habits.
     * */
    @Operation(summary = "Get habits as two lists - canBeChecked and cannotBeChecked for accountId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response"),
            @ApiResponse(responseCode = "404", description = "Habits doesn't exist"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping(value = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> get(
            @Parameter(name = "accountId", required = true) @PathVariable Long accountId) throws IOException {
        List<Habit> habits = habitService.getByAccountId(accountId);

        if(CollectionUtils.isEmpty(habits)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Habits doesn't exist");
        }

        Map<String, List<HabitDTO>> result = habitService.getCheckLists(habits, statisticService, habitMapper);

        return ResponseEntity.ok(result);
    }

    /**
     * Create a new habit.
     * Example of correct habit:
     * {
     *     "accountId": 1,
     *     "name": "Exercise",
     *     "description": "Morning workout routine",
     *     "period": "P1D"
     * }
     * */
    @Operation(summary = "Create a new habit.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habit saved successfully"),
            @ApiResponse(responseCode = "400", description = "ID must be null"),
            @ApiResponse(responseCode = "400", description = "Account ID and name are required"),
            @ApiResponse(responseCode = "400", description = "Error creating a habit"),
            @ApiResponse(responseCode = "500", description = "Failed to save habit")
    })
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(
            @Parameter(name = "habitDTO", required = true) @RequestBody HabitDTO habitDTO) {

        if (habitDTO.id() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID must be null");
        }

        if (habitDTO.accountId() == null || habitDTO.name() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account ID and name are required");
        }

        Habit habit = habitMapper.toEntity(habitDTO);

        if(habit == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating a habit");
        }

        if(habitService.save(habit)) {
            habit = habitService.get(habit.getAccountId(), habit.getName());
            Statistic statistic = new Statistic(habit.getId(), LocalDate.now(), null);
            statisticService.save(statistic);
            return ResponseEntity.ok("Habit saved successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save habit");
        }
    }

    @Operation(summary = "Update a habit.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habit updated successfully"),
            @ApiResponse(responseCode = "400", description = "ID must be null"),
            @ApiResponse(responseCode = "400", description = "Error creating a habit"),
            @ApiResponse(responseCode = "500", description = "Failed to update habit")
    })
    @PatchMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(
            @Parameter(name = "habitDTO", required = true) @RequestBody HabitDTO habitDTO) {

        if (habitDTO.accountId() == null || habitDTO.name() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account ID and name are required");
        }

        Habit habit = habitMapper.toEntity(habitDTO);

        if(habit == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating a habit");
        }

        if(habitService.update(habit)) {
            return ResponseEntity.ok("Habit updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update habit");
        }
    }

    @Operation(summary = "Delete a habit.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response"),
            @ApiResponse(responseCode = "404", description = "Habit not found"),
            @ApiResponse(responseCode = "500", description = "Failed to delete habit")
    })
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(
            @Parameter(name = "id", required = true) @PathVariable Long id) {

        Habit habit = habitService.get(id);

        if (habit == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Habit not found");
        }

        if (habitService.delete(id)) {
            return ResponseEntity.ok("Habit deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete habit");
        }
    }
}
