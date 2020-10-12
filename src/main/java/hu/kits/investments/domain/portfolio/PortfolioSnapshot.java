package hu.kits.investments.domain.portfolio;

import java.util.Map;
import java.util.Set;

import hu.kits.investments.domain.Asset;
import hu.kits.investments.domain.marketdata.AssetPrices;

public record PortfolioSnapshot(Map<Asset, Integer> assetsMap, int cash) {

    public PortfolioSnapshot {
        this.assetsMap = Map.copyOf(assetsMap);
        this.cash = cash;
    }

    public int quantity(Asset asset) {
        return assetsMap.getOrDefault(asset, 0);
    }
    
    public Set<Asset> assets() {
        return assetsMap.keySet();
    }
    
    public int portfolioValue(AssetPrices assetPrices) {
        int valueOfAssets = assetsMap.entrySet().stream().mapToInt(e -> (int)(getAssetPrice(e.getKey(), assetPrices) * e.getValue())).sum();
        return valueOfAssets + cash;
    }
    
    private static double getAssetPrice(Asset asset, AssetPrices assetPrices) {
        return assetPrices.price(asset).orElseThrow(() -> new IllegalArgumentException("Can no find price for " + asset.ticker()));
    }
}
