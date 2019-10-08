package hu.kits.investments.domain.marketdata;

public interface PriceDataRepository {

    PriceHistory getPriceHistory();
    
    boolean savePriceData(PriceData priceData);
    
}
