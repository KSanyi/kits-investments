package hu.kits.investments.domain.investment.strategy;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.kits.investments.common.CollectionsUtil;
import hu.kits.investments.domain.asset.Asset;
import hu.kits.investments.domain.investment.Allocation;
import hu.kits.investments.domain.marketdata.AssetPrices;
import hu.kits.investments.domain.marketdata.PriceHistory;
import hu.kits.investments.domain.math.MathUtil;
import hu.kits.investments.domain.portfolio.PortfolioSnapshot;
import hu.kits.investments.domain.portfolio.PortfolioValueSnapshot;
import hu.kits.investments.domain.portfolio.TradeOrder;
import hu.kits.investments.domain.portfolio.TradeOrder.Side;

public class ConstantAllocation implements InvestmentStrategy {

    private final Allocation allocation;
    
    private final int rebalanceMonths;
    
    private LocalDate lastRebalanceDate;
    
    public ConstantAllocation(Allocation allocation, int rebalanceMonths) {
        this.allocation = allocation;
        this.rebalanceMonths = rebalanceMonths;
    }

    @Override
    public List<TradeOrder> createTradeOrders(PortfolioSnapshot portfolio, PriceHistory priceHistory, LocalDate date) {
        
        if(lastRebalanceDate == null) throw new IllegalStateException("Start has not been called");
        
        if(date.equals(lastRebalanceDate.plusMonths(rebalanceMonths))) {
            lastRebalanceDate = date;
            
            AssetPrices assetPrices = priceHistory.assetPricesAt(date);
            PortfolioValueSnapshot currentPortfolioValueSnapshot = portfolio.valuate(assetPrices);
            int portfolioValue = currentPortfolioValueSnapshot.portfolioValue();
            PortfolioValueSnapshot targetPortfolioValueSnapshot = createTargetPortfolioValueSnapshot(portfolioValue);
            
            Set<Asset> assets = CollectionsUtil.union(currentPortfolioValueSnapshot.assets(), targetPortfolioValueSnapshot.assets());
            
            List<TradeOrder> tradeOrders = new ArrayList<>();
            for(Asset asset : assets) {
                int targetValue = targetPortfolioValueSnapshot.value(asset);
                int currentValue = currentPortfolioValueSnapshot.value(asset);
                BigDecimal price = assetPrices.price(asset).get();
                int diffQuantity = MathUtil.divideRound(targetValue - currentValue, price).intValue();
                if(diffQuantity > 0) {
                    tradeOrders.add(new TradeOrder(date, asset, Side.BUY, diffQuantity, price));
                } else if(diffQuantity < 0) {
                    tradeOrders.add(new TradeOrder(date, asset, Side.SELL, -diffQuantity, price));
                }
            }
            return tradeOrders;
        } else {
            return emptyList();            
        }
    }

    private PortfolioValueSnapshot createTargetPortfolioValueSnapshot(int portfolioValue) {
        
        Map<Asset, Integer> assetValueMap = allocation.assets().stream()
                .collect(toMap(asset -> asset, asset -> (int)(allocation.weightOf(asset) / 100.0 * portfolioValue)));
        
        int cash = allocation.cashWeight();
        
        return new PortfolioValueSnapshot(assetValueMap, cash);
    }

    @Override
    public List<TradeOrder> start(PriceHistory priceHistory, LocalDate startDate, int cash) {
        
        lastRebalanceDate = startDate;
        
        AssetPrices assetPrices = priceHistory.assetPricesAt(startDate);
        
        return allocation.assets().stream()
                .map(asset -> createTradeOrder(startDate, asset, allocation.weightOf(asset) / 100.0, assetPrices, cash))
                .toList();
    }
    
    private static TradeOrder createTradeOrder(LocalDate date, Asset asset, double weight, AssetPrices assetPrices, int cash) {
       
        BigDecimal unitPrice = assetPrices.price(asset).orElseThrow(() -> new IllegalArgumentException("Can not find price for " + asset.ticker() + " for " + date));
        int quantity = MathUtil.divideRound(cash * weight, unitPrice).intValue();
        
        return new TradeOrder(date, asset, Side.BUY, quantity, unitPrice);
    }
    
    @Override
    public String toString() {
        return "BuyAndHold: " + allocation;
    }

}
