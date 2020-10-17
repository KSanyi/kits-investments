package hu.kits.investments.domain.investment.strategy;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;

import hu.kits.investments.domain.Asset;
import hu.kits.investments.domain.investment.Allocation;
import hu.kits.investments.domain.investment.InvestmentStrategy;
import hu.kits.investments.domain.marketdata.AssetPrices;
import hu.kits.investments.domain.marketdata.PriceHistory;
import hu.kits.investments.domain.portfolio.PortfolioSnapshot;
import hu.kits.investments.domain.portfolio.TradeOrder;

public class BuyAndHold implements InvestmentStrategy {

    private final Allocation allocation;
    
    public BuyAndHold(Allocation allocation) {
        this.allocation = allocation;
    }

    @Override
    public List<TradeOrder> createTradeOrders(PortfolioSnapshot portfolio, PriceHistory priceHistory, LocalDate date) {
        return emptyList();
    }

    @Override
    public List<TradeOrder> start(PriceHistory priceHistory, LocalDate startDate, int cash) {
        
        AssetPrices assetPrices = priceHistory.assetPricesAt(startDate);
        
        return allocation.assets().stream()
                .map(asset -> createTradeOrder(startDate, asset, allocation.weightOf(asset) / 100.0, assetPrices, cash))
                .collect(toList());
    }
    
    private static TradeOrder createTradeOrder(LocalDate date, Asset asset, double weight, AssetPrices assetPrices, int cash) {
       
        Double unitPrice = assetPrices.price(asset).orElseThrow(() -> new IllegalArgumentException("Can not find price for " + asset.ticker() + " for " + date));
        int quantity = (int)Math.floor(cash * weight / unitPrice);
        
        return new TradeOrder(date, asset, quantity, unitPrice);
    }
    
    @Override
    public String toString() {
        return "BuyAndHold: " + allocation;
    }

}
