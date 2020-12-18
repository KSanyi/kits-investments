package hu.kits.investments.domain.portfolio;

import java.util.List;

import hu.kits.investments.common.Formatters;
import hu.kits.investments.domain.TimeSeries.TimeSeriesEntry;

public record PortfolioStats(
        TimeSeriesEntry<Integer> start, 
        TimeSeriesEntry<Integer> end,
        double yield,
        double annualYield,
        double twr,
        double annualTwr,
        double volatility,
        double sharpeRatio,
        TimeSeriesEntry<Integer> high,
        TimeSeriesEntry<Integer> low) {

    @Override
    public String toString() {
        return String.join(",\t", List.of(
               "start: " + start.date() + ": " + Formatters.formatDecimal(start.value()),
               "end: " + end.date() + ": " + Formatters.formatDecimal(end.value()),
               "diff: " + Formatters.formatDecimal(end.value() - start.value()),
               "yield: " + Formatters.formatPercent(yield),
               "annual yield: " + Formatters.formatPercent(annualYield),
               "TWR: " + Formatters.formatPercent(twr),
               "annual TWR: " + Formatters.formatPercent(annualTwr),
               "volatility: " + Formatters.formatPercent(volatility),
               "Sharp ratio: " + Formatters.formatFractionalDecimal(sharpeRatio),
               "high: " + high.date() + ": " + Formatters.formatDecimal(high.value()), 
               "low: " + low.date() + ": " + Formatters.formatDecimal(low.value())));
    }
    
}
