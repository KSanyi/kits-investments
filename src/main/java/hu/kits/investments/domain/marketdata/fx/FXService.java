package hu.kits.investments.domain.marketdata.fx;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.investments.common.Clock;
import hu.kits.investments.domain.marketdata.fx.FXRates.FXRate;

public class FXService {
    
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final FXRateRepository fxRepository;
    
    private final FXRateWebService fxRateWebService;
    
    public FXService(FXRateRepository fxRepository, FXRateWebService fxRateWebService) {
        this.fxRepository = fxRepository;
        this.fxRateWebService = fxRateWebService;
    }
    
    public List<FXRate> downloadDailyFxRates() {
        LocalDate latestPriceDate = fxRepository.getLatestPriceDate();
        if(latestPriceDate.equals(Clock.today())) {
            log.info("We have today's FX rates, no need to download");
            return List.of();
        }
        List<FXRate> dailyFXRates = fxRateWebService.getDailyFXRates(latestPriceDate.plusDays(1), Clock.today());
        if(!dailyFXRates.isEmpty()) {
            fxRepository.saveFXRates(dailyFXRates);
            log.info("{} fx rates saved", dailyFXRates.size());
        } else {
            log.warn("No daily FX rates received");
        }
        
        return dailyFXRates;
    }

    public FXRates getFXRates() {
        return fxRepository.loadFXRates();
    }
    
}
