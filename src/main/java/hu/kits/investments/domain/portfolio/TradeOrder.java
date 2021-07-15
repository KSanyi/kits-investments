package hu.kits.investments.domain.portfolio;

import java.math.BigDecimal;
import java.time.LocalDate;

import hu.kits.investments.domain.asset.Asset;

public record TradeOrder(LocalDate date, Asset asset, Side side, int quantity, BigDecimal unitPrice) {

    public int value() {
        return unitPrice.multiply(new BigDecimal(quantity)).intValue();
    }
    
    public int signedValue() {
        return unitPrice.multiply(new BigDecimal(signedQuantity())).intValue();
    }
    
    public int signedQuantity() {
        return side == Side.BUY ? quantity : -quantity;
    }
    
    public static enum Side {
        BUY, SELL
    }
    
}
