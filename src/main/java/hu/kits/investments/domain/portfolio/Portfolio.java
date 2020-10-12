package hu.kits.investments.domain.portfolio;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.kits.investments.domain.Asset;
import hu.kits.investments.domain.marketdata.AssetPrices;

public class Portfolio {

    private final List<TradeOrder> tradeOrders = new ArrayList<>();
    
    private final int startCash;
    
    public Portfolio(int startCash) {
        this.startCash = startCash;
    }
    
    public int cash() {
        return cashAt(LocalDate.MAX);
    }
    
    public int cashAt(LocalDate date) {
        return startCash - tradeOrders.stream()
                .filter(tradeOrder -> !tradeOrder.date().isAfter(date))
                .mapToInt(TradeOrder::value)
                .sum();
    }
    
    public int quantity(Asset asset) {
        return quantityAt(asset, LocalDate.MAX);
    }
    
    public int quantityAt(Asset asset, LocalDate date) {
        return tradeOrders.stream()
                .filter(tradeOrder -> tradeOrder.asset().equals(asset))
                .filter(tradeOrder -> !tradeOrder.date().isAfter(date))
                .reduce(Position.createEmpty(asset), Position::update, Position::combine).quantity();
    }
    
    public void buy(LocalDate date, Asset asset, int quantity, double unitPrice) {
        if(cashAt(date) < unitPrice * quantity) throw new IllegalStateException("Do not have " + unitPrice + " USD cash in portfolio");
        tradeOrders.add(new TradeOrder(date, asset, quantity, unitPrice));
    }
    
    public void sell(LocalDate date, Asset asset, int quantity, double unitPrice) {
        if(quantityAt(asset, date) < quantity) throw new IllegalStateException("Do not have " + quantity + " " + asset + " in portfolio");
        tradeOrders.add(new TradeOrder(date, asset, -quantity, unitPrice));
    }
    
    public PortfolioSnapshot createSnapshot() {
        return createSnapshotAt(LocalDate.MAX);
    }
    
    public PortfolioSnapshot createSnapshotAt(LocalDate date) {
        Map<Asset, Integer> assetQuantityMap = tradeOrders.stream()
            .filter(tradeOrder -> !tradeOrder.date().isAfter(date))
            .map(TradeOrder::asset)
            .distinct()
            .collect(toMap(asset -> asset, asset -> quantityAt(asset, date)));
        
        return new PortfolioSnapshot(assetQuantityMap, cashAt(date));
    }

    public int portfolioValue(AssetPrices assetPricesAt) {
        return portfolioValueAt(LocalDate.MAX, assetPricesAt);
    }
    
    public int portfolioValueAt(LocalDate date, AssetPrices assetPrices) {
        return createSnapshotAt(date).portfolioValue(assetPrices);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
