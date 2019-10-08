package hu.kits.investments.domain.marketdata;

import java.util.List;

import hu.kits.investments.common.DateRange;

public interface PriceDataSource {

    List<PriceData> getPriceData(String ticker, DateRange dateRange);
    
}
