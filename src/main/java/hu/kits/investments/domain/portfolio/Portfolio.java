package hu.kits.investments.domain.portfolio;

import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.asset.Asset;
import hu.kits.investments.domain.marketdata.AssetPrices;
import hu.kits.investments.domain.portfolio.TradeOrder.Side;

public class Portfolio {

    private final List<TradeOrder> tradeOrders = new ArrayList<>();
    public final List<CashMovement> cashMovements = new ArrayList<>();
    
    public void deposit(LocalDate date, int amount) {
        cashMovements.add(new CashMovement(date, amount));
    }
    
    public void withdraw(LocalDate date, int amount) {
        cashMovements.add(new CashMovement(date, -amount));
    }
    
    public int cash() {
        return cashAt(LocalDate.MAX);
    }
    
    public int cashAt(LocalDate date) {
        
        int netTradeCashMovements = tradeOrders.stream()
            .filter(tradeOrder -> !tradeOrder.date().isAfter(date))
            .mapToInt(TradeOrder::signedValue)
            .sum();
        
        int netCashMovements = cashMovements.stream()
            .filter(cashMovement -> !cashMovement.date().isAfter(date))
            .mapToInt(CashMovement::amount)
            .sum();
        
        return netCashMovements - netTradeCashMovements; 
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
    
    public void buy(LocalDate date, Asset asset, int quantity, BigDecimal unitPrice) {
        
        if(quantity <= 0) throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        
        BigDecimal value = unitPrice.multiply(new BigDecimal(quantity));
        
        if(cashAt(date) + 200 < value.intValue()) throw new IllegalStateException("No " + value + " USD cash in portfolio, only " + cashAt(date));
        tradeOrders.add(new TradeOrder(date, asset, Side.BUY, quantity, unitPrice));
    }
    
    public void sell(LocalDate date, Asset asset, int quantity, BigDecimal unitPrice) {
        
        if(quantity <= 0) throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        
        if(quantityAt(asset, date) < quantity) throw new IllegalStateException("No " + quantity + " " + asset + " in portfolio");
        tradeOrders.add(new TradeOrder(date, asset, Side.SELL, quantity, unitPrice));
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
    
    public int netCashMovementAt(LocalDate date) {
        return cashMovements.stream().filter(c -> c.date().equals(date)).mapToInt(CashMovement::amount).sum();
    }
    
    public int netCashMovementAt(DateRange dateRange) {
        return cashMovements.stream().filter(c -> dateRange.contains(c.date())).mapToInt(CashMovement::amount).sum();
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
