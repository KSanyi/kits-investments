package hu.kits.investments.domain.portfolio;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Map;

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
        VaRAndES varAndES95,
        VaRAndES varAndES99,
        TimeSeriesEntry<Integer> high,
        TimeSeriesEntry<Integer> low,
        Map<Integer, Double> recentMonthsPerformance) {

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
               "VaR 95: " + Formatters.formatFractionalDecimal(varAndES95.var),
               "ES 95: " + Formatters.formatFractionalDecimal(varAndES95.es),
               "VaR 99: " + Formatters.formatFractionalDecimal(varAndES99.var),
               "ES 99: " + Formatters.formatFractionalDecimal(varAndES99.es),
               "high: " + high.date() + ": " + Formatters.formatDecimal(high.value()), 
               "low: " + low.date() + ": " + Formatters.formatDecimal(low.value()),
               "recent months performance: " + printRecentMonthsPerformance()));
    }
    
    private String printRecentMonthsPerformance() {
        return recentMonthsPerformance.keySet().stream()
            .sorted()
            .map(month -> month + ": " + Formatters.formatPercent(recentMonthsPerformance.get(month)))
            .collect(joining(", "));
    }
    
    public static record VaRAndES(double var, double es) {}
    
}
