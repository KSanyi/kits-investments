package hu.kits.investments.domain.portfolio;

import java.math.BigDecimal;
import java.time.LocalDate;

import hu.kits.investments.domain.asset.Asset;

public record TradeOrder(LocalDate date, Asset asset, int quantity, BigDecimal unitPrice) {

    public int value() {
        return unitPrice.multiply(new BigDecimal(quantity)).intValue();
    }
    
}
