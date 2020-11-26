package hu.kits.investments.domain.marketdata;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import hu.kits.investments.domain.asset.Asset;

public class AssetPrices {

    private final Map<Asset, Double> priceMap;

    public AssetPrices(Map<Asset, Double> priceMap) {
        this.priceMap = priceMap;
    }
    
    public Optional<Double> price(Asset asset) {
        return Optional.ofNullable(priceMap.get(asset));
    }
    
    public Set<Asset> tickers() {
        return priceMap.keySet();
    }
    
}
