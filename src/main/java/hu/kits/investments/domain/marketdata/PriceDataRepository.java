package hu.kits.investments.domain.marketdata;

import java.util.List;

import hu.kits.investments.domain.Asset;

public interface PriceDataRepository {

    PriceHistory getPriceHistory(List<Asset> assets);
    
    boolean savePriceData(PriceData priceData);
    
}
