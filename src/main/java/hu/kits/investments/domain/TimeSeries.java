package hu.kits.investments.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

public class TimeSeries<T> {

    private final SortedMap<LocalDate, T> dailyValues;
    
    public TimeSeries(Map<LocalDate, T> values) {
        this.dailyValues = new TreeMap<>(values);
    }

    public T effectiveValueAt(LocalDate date) {
        TimeSeriesEntry<T> effectiveEntryAt = effectiveEntryAt(date);
        return effectiveEntryAt != null ? effectiveEntryAt.value : null;
    }
    
    public TimeSeriesEntry<T> effectiveEntryAt(LocalDate date) {
        T value = dailyValues.getOrDefault(date, null);
        for(int i=0;i<10;i++) {
            value = dailyValues.get(date.minusDays(i));
            if(value != null) {
                return new TimeSeriesEntry<>(date, value);
            }
        }
        return null;
    }
    
    public List<T> values() {
        return List.copyOf(dailyValues.values());
    }
    
    public TimeSeriesEntry<T> firstEntry() {
        LocalDate firstDate = dailyValues.firstKey();
        return new TimeSeriesEntry<>(firstDate, dailyValues.get(firstDate));
    }
    
    public TimeSeriesEntry<T> lastEntry() {
        LocalDate lastDate = dailyValues.lastKey();
        return new TimeSeriesEntry<>(lastDate, dailyValues.get(lastDate));
    }
    
    public Stream<TimeSeriesEntry<T>> stream() {
        return dailyValues.entrySet().stream().map(e -> new TimeSeriesEntry<>(e.getKey(), e.getValue()));
    }
    
    public static record TimeSeriesEntry<T>(LocalDate date, T value) {}
    
}
