package hu.kits.investments.common;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class DateRange implements Comparable<DateRange>, Iterable<LocalDate> {

    private static final LocalDate MAX = LocalDate.of(2050,1,1);
    
	public static DateRange defaultValue() {
	    return new DateRange(Clock.today().withDayOfYear(1), Clock.today());
	}
	
	public static DateRange thisMonth(LocalDate today) {
        return new DateRange(today.withDayOfMonth(1), today.plusMonths(1).withDayOfMonth(1).minusDays(1));
    }
	
	public static DateRange thisYear(LocalDate today) {
        return new DateRange(today.withDayOfYear(1), today);
    }
	
	public static DateRange openUntilToday() {
        return new DateRange(LocalDate.MIN, Clock.today());
    }
	
	public static DateRange from(LocalDate dateFrom) {
	    return new DateRange(dateFrom, Clock.today());
    }
	
	public LocalDate from;
	
	public LocalDate to;

	public DateRange(LocalDate from, LocalDate to) {
		if(from == null) from = LocalDate.MIN;
		if(to == null) to = MAX;
		if(to.isBefore(from)) {
			throw new IllegalArgumentException("Invalid interval: " + from + " - " + to);
		}
		this.from = from;
		this.to = to;
	}
	
	public boolean contains(LocalDate value) {
		return !value.isBefore(from) && !value.isAfter(to);
	}
	
	public boolean contains(DateRange other) {
        return !this.from.isAfter(other.from) && !this.to.isBefore(other.to);
    }
	
	public List<LocalDate> days() {
	    List<LocalDate> days = new ArrayList<>();
	    LocalDate date = from;
	    while(!date.isAfter(to)) {
	        days.add(date);
	        date = date.plusDays(1);
	    }
	    return Collections.unmodifiableList(days);
	}
	
	 public Stream<LocalDate> stream() {
	        return days().stream();
	    }
	
	public int numberOfDays() {
	    return (int)Duration.between(from.atStartOfDay(), to.atStartOfDay()).toDays();
	}
	
	public DateRange shiftLeft(int days) {
	    return new DateRange(from.plusDays(days), to);
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == this) return true;
		if(other == null || !(other instanceof DateRange)) return false;
		DateRange otherInterval = (DateRange)other;
		return from == otherInterval.from && to == otherInterval.to;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(from, to);
	}
	
	@Override
    public String toString() {
		String fromString = from .equals(LocalDate.MIN) ? "" : from.toString();
		String toString = from .equals(MAX) ? "" : to.toString();
        return "[" + fromString + " - " + toString + "]";
    }

	@Override
	public int compareTo(DateRange other) {
		return from.compareTo(other.from);
	}

    @Override
    public Iterator<LocalDate> iterator() {
        return new Iterator<>() {

            private LocalDate date = from;
            
            @Override
            public boolean hasNext() {
                return date.isBefore(to);
            }

            @Override
            public LocalDate next() {
                date = date.plusDays(1);
                return date;
            }
        };
    }

}
	
