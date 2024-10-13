package io.envoi.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.Objects;

public class Habit implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Period period;
    private LinkedHashMap<LocalDate, Boolean> statistic;

    public Habit(String name, String description, Period period, LinkedHashMap<LocalDate, Boolean> statistic)
    {
        setName(name);
        setDescription(description);
        setPeriod(period);
        setStatistic(statistic);
    }
    public Habit(String name, String description, Period period)
    {
        setName(name);
        setDescription(description);
        setPeriod(period);
        statistic = new LinkedHashMap<>();
        statistic.put(LocalDate.now(), null);
    }

    public void check()
    {
        LocalDate today = LocalDate.now();
        statistic.put(today, true);
    }
    private LocalDate getLastDate()
    {
        return statistic.keySet().stream().reduce((first, second) -> second).orElse(null);
    }

    public Boolean getLastStatistic()
    {
        return statistic.entrySet().stream().reduce((first, second) -> second).orElse(null).getValue();
    }
    public boolean canBeChecked()
    {
        LocalDate today = LocalDate.now();
        LocalDate lastDate = getLastDate();

        Boolean lastStatus = statistic.get(lastDate);

        // Доставляем пропущенные записи до сегодняшнего дня
        while (lastDate.isBefore(today)) {
            lastDate = lastDate.plus(period);
            if (lastDate.isBefore(today)) {
                statistic.put(lastDate, false); // Пропущенные дни
            } else if (lastDate.equals(today)) {
                statistic.put(lastDate, null); // Сегодняшний день
            }
        }

        return getLastStatistic() == null;
    }
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Period getPeriod()
    {
        return period;
    }

    public void setPeriod(Period period)
    {
        this.period = period;
    }

    public LinkedHashMap<LocalDate, Boolean> getStatistic()
    {
        return statistic;
    }

    public void setStatistic(LinkedHashMap<LocalDate, Boolean> statistic)
    {
        this.statistic = statistic;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Habit habit = (Habit) o;
        return name.equals(habit.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name);
    }
}