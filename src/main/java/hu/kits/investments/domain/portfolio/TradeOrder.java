package hu.kits.investments.domain.portfolio;

import java.time.LocalDate;

import hu.kits.investments.domain.Asset;

public record TradeOrder(LocalDate date, Asset asset, int quantity, double unitPrice) {

    public int value() {
        return (int)(quantity * unitPrice);
    }
    
}
