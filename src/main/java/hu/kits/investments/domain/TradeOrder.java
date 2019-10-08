package hu.kits.investments.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TradeOrder {

    public final Asset asset;
    
    public final int quantity;
    
    public final double unitPrice;

    public TradeOrder(Asset asset, int quantity, double unitPrice) {
        this.asset = asset;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    public int price() {
        return (int)(quantity * unitPrice);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
