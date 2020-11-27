package hu.kits.investments.domain.marketdata.fx;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.investments.common.Amount;
import hu.kits.investments.domain.asset.Currency;

public class FXRates {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public final Map<Currency, Map<LocalDate, BigDecimal>> ratesMap;
    
    public FXRates(List<FXRate> fxRates) {
        
        ratesMap = fxRates.stream().collect(groupingBy(FXRate::currency, toMap(
                FXRate::date,
                FXRate::rateInHUF,
                (o1, o2) -> o1,
                TreeMap::new)));
    }
    
    public Optional<FXRate> get(Currency currency, LocalDate date) {
        var datesRateMap = ratesMap.get(currency);
        
        if(datesRateMap == null) {
            log.warn("No FX rates for {}", currency);
            return Optional.empty();
        }
        
        LocalDate d = date;
        for(int i=1;i<=5;i++) {
            BigDecimal rate = datesRateMap.get(d);
            if(rate != null) {
                return Optional.of(new FXRate(date, currency, rate));
            }
            d = date.minusDays(i);
        }
        return Optional.empty();
    }
    
    public static record FXRate(LocalDate date, Currency currency, BigDecimal rateInHUF) {
        
        public Amount convert(Amount amount) {
            if(amount.currency() != currency) throw new IllegalArgumentException("Illegal currency for conversion");
            return new Amount(Currency.HUF, amount.value().multiply(rateInHUF));
        }
    }
    
}
