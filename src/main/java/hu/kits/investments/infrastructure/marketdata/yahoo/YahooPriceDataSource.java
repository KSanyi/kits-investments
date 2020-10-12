package hu.kits.investments.infrastructure.marketdata.yahoo;

import static java.util.stream.Collectors.toList;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.Asset;
import hu.kits.investments.domain.marketdata.PriceData;
import hu.kits.investments.domain.marketdata.PriceDataSource;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class YahooPriceDataSource implements PriceDataSource {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    @Override
    public List<PriceData> getPriceData(String ticker, DateRange dateRange) {
        
        try {
            Stock stock = YahooFinance.get(ticker, toCalendar(dateRange.from), toCalendar(dateRange.to), Interval.DAILY);
            if(stock != null) {
                return stock.getHistory().stream()
                        .map(YahooPriceDataSource::getPriceData)
                        .flatMap(Optional::stream)
                        .collect(toList());    
            } else {
                logger.error("No price data found for: " + ticker);
                return List.of();
            }
        } catch (Exception ex) {
            logger.error("Error geting price data for {} {}: {}", ticker, dateRange, ex.getMessage());
            return Collections.emptyList();
        }
    }
    
    private static Optional<PriceData> getPriceData(HistoricalQuote historicalQuote) {
        if(historicalQuote.getClose() != null) {
            return Optional.of(new PriceData(
                    new Asset(historicalQuote.getSymbol()), 
                    toLocalDate(historicalQuote.getDate()),
                    historicalQuote.getClose().doubleValue()));
        } else {
            return Optional.empty();
        }
    }
    
    private static Calendar toCalendar(LocalDate date) {
        return GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault()));
    }
    
    private static LocalDate toLocalDate(Calendar calendar) {
        return calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
