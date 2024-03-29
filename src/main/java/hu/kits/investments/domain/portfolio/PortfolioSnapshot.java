package hu.kits.investments.domain.portfolio;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import hu.kits.investments.common.Formatters;
import hu.kits.investments.domain.asset.Asset;
import hu.kits.investments.domain.marketdata.AssetPrices;

public record PortfolioSnapshot(Map<Asset, Integer> assetsMap, int cash) {

    /*
    public PortfolioSnapshot {
        this.assetsMap = Map.copyOf(assetsMap);
        this.cash = cash;
    }
    */

    public int quantity(Asset asset) {
        return assetsMap.getOrDefault(asset, 0);
    }
    
    public Set<Asset> assets() {
        return assetsMap.keySet();
    }
    
    public int portfolioValue(AssetPrices assetPrices) {
        int valueOfAssets = assetsMap.entrySet().stream()
                .mapToInt(e -> getAssetPrice(e.getKey(), assetPrices).multiply(new BigDecimal(e.getValue())).intValue())
                .sum();
        return valueOfAssets + cash;
    }
    
    public PortfolioValueSnapshot valuate(AssetPrices assetPrices) {
        Map<Asset, Integer> assetValueMap = assetsMap.entrySet().stream().collect(toMap(
                e -> e.getKey(), 
                e -> getAssetPrice(e.getKey(), assetPrices).multiply(new BigDecimal(e.getValue())).intValue()));
        
        return new PortfolioValueSnapshot(assetValueMap, cash);
        
    }
    
    private static BigDecimal getAssetPrice(Asset asset, AssetPrices assetPrices) {
        return assetPrices.price(asset).orElseThrow(() -> new IllegalArgumentException("Can not find price for " + asset.ticker()));
    }
    
    @Override
    public String toString() {
        String positionsString = assetsMap.entrySet().stream()
                .map(e -> e.getKey().ticker() + ": " + Formatters.formatDecimal(e.getValue()))
                .collect(joining(", "));
        
        return positionsString + ", cash: " + Formatters.formatDecimal(cash);
    }
}
