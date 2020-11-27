package hu.kits.investments.domain.marketdata;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import hu.kits.investments.domain.asset.Asset;

public class AssetPrices {

    private final Map<Asset, BigDecimal> priceMap;

    public AssetPrices(Map<Asset, BigDecimal> priceMap) {
        this.priceMap = priceMap;
    }
    
    public Optional<BigDecimal> price(Asset asset) {
        return Optional.ofNullable(priceMap.get(asset));
    }
    
    public Set<Asset> tickers() {
        return priceMap.keySet();
    }
    
}
