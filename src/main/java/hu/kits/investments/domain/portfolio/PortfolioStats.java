package hu.kits.investments.domain.portfolio;

import java.util.List;

import hu.kits.investments.common.Formatters;
import hu.kits.investments.domain.TimeSeries.TimeSeriesEntry;

public record PortfolioStats(
        TimeSeriesEntry<Integer> start, 
        TimeSeriesEntry<Integer> end,
        double yield,
        double yearlyYield,
        double volatility,
        TimeSeriesEntry<Integer> high,
        TimeSeriesEntry<Integer> low) {

    @Override
    public String toString() {
        return String.join(",\t", List.of(
               "start: " + start.date() + ": " + Formatters.formatDecimal(start.value()),
               "end: " + end.date() + ": " + Formatters.formatDecimal(end.value()),
               "yield: " + Formatters.formatPercent(yield),
               "yearly yield: " + Formatters.formatPercent(yearlyYield),
               "volatility: " + Formatters.formatPercent(volatility),
               "high: " + high.date() + ": " + Formatters.formatDecimal(high.value()), 
               "low: " + low.date() + ": " + Formatters.formatDecimal(low.value())));
    }
    
}
