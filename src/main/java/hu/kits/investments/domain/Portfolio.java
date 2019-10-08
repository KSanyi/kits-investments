package hu.kits.investments.domain;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.kits.investments.domain.marketdata.AssetPrices;

public class Portfolio {

    private final Map<Asset, Integer> assets = new HashMap<>();
    
    private int cash;
    
    public Portfolio(int cash) {
        this.cash = cash;
    }
    
    public int cash() {
        return cash;
    }
    
    public int quantity(Asset asset) {
        return assets.getOrDefault(asset, 0);
    }
    
    public void buy(Asset asset, int quantity, int price) {
        if(cash < price) throw new IllegalStateException("Do not have " + price + " USD cash in portfolio");
        assets.merge(asset, quantity, (q1, q2) -> q1 + q2);
        cash -= price;
    }
    
    public void sell(Asset asset, int quantity, int price) {
        if(assets.get(asset) < quantity) throw new IllegalStateException("Do not have " + quantity + " " + asset + " in portfolio");
        assets.merge(asset, quantity, (q1, q2) -> q1 - q2);
        cash += price;
    }
    
    public PortfolioSnapshot createSnapshot() {
        return new PortfolioSnapshot(assets, cash);
    }

    public int value(AssetPrices assetPricesAt) {
        int valueOfAssets = assets.entrySet().stream().mapToInt(e -> (int)assetPricesAt.price(e.getKey()) * e.getValue()).sum();
        return valueOfAssets + cash;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
