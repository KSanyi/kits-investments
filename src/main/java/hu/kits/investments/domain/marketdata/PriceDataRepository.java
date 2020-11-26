package hu.kits.investments.domain.marketdata;

import hu.kits.investments.domain.asset.Assets;

public interface PriceDataRepository {

    PriceHistory getPriceHistory(Assets assets);
    
    boolean savePriceData(PriceData priceData);
    
}
