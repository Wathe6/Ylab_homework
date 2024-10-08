package io.envoi.model;

import java.time.Period;
import java.util.Date;
import java.util.Map;

public class Habit
{
    private String name;
    private String description;
    private Period period;
    private Map<Date, Boolean> statistic;
}
