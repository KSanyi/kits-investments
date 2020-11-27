package hu.kits.investments.domain.marketdata.fx;

import java.time.LocalDate;
import java.util.List;

import hu.kits.investments.domain.marketdata.fx.FXRates.FXRate;

public interface FXRateRepository {

    FXRates loadFXRates();
    
    void saveFXRates(List<FXRate> fxRates);

    LocalDate getLatestPriceDate();
    
}
