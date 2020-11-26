package hu.kits.investments.domain.marketdata;

import java.util.List;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.asset.Asset;

public interface PriceDataSource {

    List<PriceData> getPriceData(Asset asset, DateRange dateRange);
    
}
