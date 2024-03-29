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
import hu.kits.investments.domain.portfolio.PortfolioStats.VaRAndES;

public class PortfolioStatsCreator {

    private static final double RISK_FREE_RETURN = 0.05;
    
    public static PortfolioStats createPortfolioStats(Portfolio portfolio, PriceHistory priceHistory) {
        
        Map<LocalDate, Integer> porfolioValues = priceHistory.dateRange().stream().collect(toMap(
                date -> date,
                date -> portfolio.portfolioValueAt(date, priceHistory.assetPricesAt(date))));
        
        TimeSeries<Integer> valueHistory = new TimeSeries<>(porfolioValues);
        
        TimeSeriesEntry<Integer> start = valueHistory.firstEntry();
        TimeSeriesEntry<Integer> end = valueHistory.lastEntry();
        
        double yield = (end.value() - start.value()) / (double)start.value();
        double annualYield = annualize(yield, start.date(), end.date());
        
        double twr = calculateTwr(portfolio, valueHistory);
        double annualTwr = annualize(twr, start.date(), end.date());
        
        List<Double> dailyYields = calculateDailyYields(portfolio, priceHistory);
        double volatility = KitsStat.stDev(dailyYields) * Math.sqrt(250);
        
        double sharpRatio = (twr - RISK_FREE_RETURN) / volatility;
        
        VaRAndES varAndES95 = VaRCalculator.calculateVaRAndES(dailyYields, 95);
        VaRAndES varAndES99 = VaRCalculator.calculateVaRAndES(dailyYields, 99);
        
        TimeSeriesEntry<Integer> high = findHigh(valueHistory);
        TimeSeriesEntry<Integer> low = findLow(valueHistory);
        
        Map<Integer, Double> recentMonthsPerformance = calculateRecentMonthsPerformance(valueHistory);
        
        return new PortfolioStats(start, end, yield, annualYield, twr, annualTwr, volatility, sharpRatio, 
                varAndES95, varAndES99, high, low, recentMonthsPerformance);
    }
    
    private static Map<Integer, Double> calculateRecentMonthsPerformance(TimeSeries<Integer> valueHistory) {
        return List.of(1, 6, 12).stream()
                .collect(toMap(month -> month, month -> calculateRecentMonthsPerformance(month, valueHistory)));
    }

    private static double calculateRecentMonthsPerformance(int month, TimeSeries<Integer> valueHistory) {
        
        LocalDate lastDate = valueHistory.lastEntry().date();
        double lastValue = valueHistory.effectiveValueAt(lastDate);
        LocalDate referenceDate = lastDate.minusMonths(month);
        Integer referenceValue = valueHistory.effectiveValueAt(referenceDate);
        
        if(referenceValue != null) {
            double yield = (lastValue - referenceValue) / (double)referenceValue;
            return annualize(yield, referenceDate, lastDate);
        } else {
            return 0;
        }
    }
    
    private static double annualize(double yield, LocalDate start, LocalDate end) {
        
        long days = ChronoUnit.DAYS.between(start, end);
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
    
    private static List<Double> calculateDailyYields(Portfolio portfolio, PriceHistory priceHistory) {
        
        List<Double> result = new ArrayList<>();
        List<LocalDate> dates = priceHistory.dates();
        for(int i=1;i<dates.size();i++) {
            LocalDate date = dates.get(i);
            LocalDate prevDate = dates.get(i-1);
            double valueForDay = portfolio.portfolioValueAt(date, priceHistory.assetPricesAt(date));
            double cashMovement = portfolio.netCashMovementAt(date);
            double valueForPrevDay = portfolio.portfolioValueAt(prevDate, priceHistory.assetPricesAt(prevDate));
            double dailyYield = (valueForDay - valueForPrevDay - cashMovement) / valueForPrevDay;
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
