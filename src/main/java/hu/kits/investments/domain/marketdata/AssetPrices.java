package hu.kits.investments.domain.marketdata;

import java.util.Map;
import java.util.Set;

import hu.kits.investments.domain.Asset;

public class AssetPrices {

    private final Map<Asset, Double> priceMap;

    public AssetPrices(Map<Asset, Double> priceMap) {
        this.priceMap = priceMap;
    }
    
    public double price(Asset asset) {
        return priceMap.get(asset);
    }
    
    public Set<Asset> tickers() {
        return priceMap.keySet();
    }
    
}
