package hu.kits.investments.domain.portfolio;

import static java.util.stream.Collectors.joining;

import java.util.Map;
import java.util.Set;

import hu.kits.investments.common.Formatters;
import hu.kits.investments.domain.asset.Asset;

public record PortfolioValueSnapshot(Map<Asset, Integer> assetsMap, int cash) {

    public PortfolioValueSnapshot {
        this.assetsMap = Map.copyOf(assetsMap);
        this.cash = cash;
    }

    public int value(Asset asset) {
        return assetsMap.getOrDefault(asset, 0);
    }
    
    public Set<Asset> assets() {
        return assetsMap.keySet();
    }
    
    public int portfolioValue() {
        int valueOfAssets = assetsMap.values().stream().mapToInt(Integer::intValue).sum();
        return valueOfAssets + cash;
    }
    
    @Override
    public String toString() {
        double portfolioValue = portfolioValue();
        String positionsString = assetsMap.entrySet().stream()
                .map(e -> e.getKey().ticker() + ": " + Formatters.formatDecimal(e.getValue()) + "(" + Formatters.formatPercent(e.getValue() / portfolioValue) + ")")
                .collect(joining(", "));
        
        return Formatters.formatDecimal(portfolioValue) + " " + positionsString + ", cash: " + Formatters.formatDecimal(cash);
    }
}
