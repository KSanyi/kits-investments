package hu.kits.investments.domain.portfolio;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.common.Pair;
import hu.kits.investments.domain.TimeSeries;
import hu.kits.investments.domain.TimeSeries.TimeSeriesEntry;
import hu.kits.investments.domain.marketdata.PriceHistory;
import hu.kits.investments.domain.math.KitsStat;

public class PortfolioStatsCreator {

    public static PortfolioStats createPortfolioStats(Portfolio portfolio, PriceHistory priceHistory) {
        
        Map<LocalDate, Integer> porfolioValues = priceHistory.dateRange().stream().collect(toMap(
                date -> date,
                date -> portfolio.portfolioValueAt(date, priceHistory.assetPricesAt(date))));
        
        TimeSeries<Integer> valueHistory = new TimeSeries<>(porfolioValues);
        
        TimeSeriesEntry<Integer> start = valueHistory.firstEntry();
        TimeSeriesEntry<Integer> end = valueHistory.lastEntry();
        
        double yield = (end.value() - start.value()) / (double)start.value();
        double annualYield = annualize(yield, start, end);
        
        double twr = calculateTwr(portfolio, valueHistory);
        double annualTwr = annualize(twr, start, end);
        
        List<Double> dailyYields = calculateDailyYields(valueHistory.values());
        double volatility = KitsStat.stDev(dailyYields) * Math.sqrt(250);
        
        TimeSeriesEntry<Integer> high = findHigh(valueHistory);
        TimeSeriesEntry<Integer> low = findLow(valueHistory);
        
        return new PortfolioStats(start, end, yield, annualYield, twr, annualTwr, volatility, high, low);
    }
    
    private static double annualize(double yield, TimeSeriesEntry<Integer> start, TimeSeriesEntry<Integer> end) {
        
        long days = ChronoUnit.DAYS.between(start.date(), end.date());
        return Math.pow(1 + yield, 365.0 / days) - 1;
    }
    
    private static double calculateTwr(Portfolio portfolio, TimeSeries<Integer> valueHistory) {
        
        List<Pair<DateRange, Integer>> periods = new ArrayList<>();
        for(int i=0;i<portfolio.cashMovements.size()-1;i++) {
            CashMovement cashMovement = portfolio.cashMovements.get(i);
            CashMovement nextCashMovement = portfolio.cashMovements.get(i+1);
            periods.add(new Pair<>(new DateRange(cashMovement.date(), nextCashMovement.date()), nextCashMovement.amount()));
        }
        CashMovement cashMovement = portfolio.cashMovements.get(portfolio.cashMovements.size()-1);
        periods.add(new Pair<>(new DateRange(cashMovement.date(), valueHistory.lastEntry().date()), 0));
        
        System.out.println(periods);
        
        List<Double> twrs = new ArrayList<>();
        for(var period : periods) {
            int startValue = valueHistory.effectiveValueAt(period.value1().from);
            int endValue = valueHistory.effectiveValueAt(period.value1().to);
            int cashflow = period.value2();
            double r = (endValue - startValue - cashflow) / (double)startValue;
            twrs.add(r);
        }
        
        return twrs.stream().mapToDouble(r -> 1+r).reduce((a, b) -> a*b).orElse(1) - 1;
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
