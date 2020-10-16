package hu.kits.investments.domain.portfolio;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hu.kits.investments.domain.marketdata.PriceHistory;
import hu.kits.investments.domain.math.KitsStat;
import hu.kits.investments.domain.portfolio.TimeSeries.TimeSeriesEntry;

public class PortfolioStatsCreator {

    public static PortfolioStats createPortfolioStats(Portfolio portfolio, PriceHistory priceHistory) {
        
        Map<LocalDate, Integer> porfolioValues = priceHistory.dates().stream().collect(toMap(
                date -> date,
                date -> portfolio.portfolioValue(priceHistory.assetPricesAt(date))));
        
        TimeSeries<Integer> valueHistory = new TimeSeries<>(porfolioValues);
        
        TimeSeriesEntry<Integer> start = valueHistory.firstEntry();
        TimeSeriesEntry<Integer> end = valueHistory.lastEntry();
        
        double yield = (end.value() - start.value()) / (double)start.value();
        double yearlyYield = calculateYearlyYield(yield, start, end);
        
        List<Double> dailyYields = calculateDailyYields(valueHistory.values());
        double volatility = KitsStat.stDev(dailyYields) * Math.sqrt(250);
        
        TimeSeriesEntry<Integer> high = findHigh(valueHistory);
        TimeSeriesEntry<Integer> low = findLow(valueHistory);
        
        return new PortfolioStats(start, end, yield, yearlyYield, volatility, high, low);
    }
    
    private static double calculateYearlyYield(double yield, TimeSeriesEntry<Integer> start, TimeSeriesEntry<Integer> end) {
        
        long days = ChronoUnit.DAYS.between(start.date(), end.date());
        return Math.pow(1 + yield, 365.0 / days) - 1;
    }
    
    private static List<Double> calculateDailyYields(List<Integer> values) {
        
        List<Double> result = new ArrayList<>();
        for(int i=1;i<values.size();i++) {
            double valueForDay = values.get(i);
            double valueForPrevDay = values.get(i-1);
            double dailyYield = (valueForDay - valueForPrevDay) / valueForPrevDay - 1;
            result.add(dailyYield);
        }
        return result;
    }
    
    private static TimeSeriesEntry<Integer> findHigh(TimeSeries<Integer> valueHistory) {
        return valueHistory.stream().max(comparing(TimeSeriesEntry::value)).get();
    }
    
    private static TimeSeriesEntry<Integer> findLow(TimeSeries<Integer> valueHistory) {
        return valueHistory.stream().min(comparing(TimeSeriesEntry::value)).get();
    }
    
}
