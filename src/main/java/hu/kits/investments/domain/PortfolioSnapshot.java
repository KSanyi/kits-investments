package hu.kits.investments.domain;

import java.util.Map;
import java.util.Set;

public class PortfolioSnapshot {

    private final Map<Asset, Integer> assets;
    
    public final int cash;
    
    public PortfolioSnapshot(Map<Asset, Integer> assets, int cash) {
        this.assets = Map.copyOf(assets);
        this.cash = cash;
    }

    public int quantity(Asset asset) {
        return assets.getOrDefault(asset, 0);
    }
    
    public Set<Asset> assets() {
        return assets.keySet();
    }
    
}
