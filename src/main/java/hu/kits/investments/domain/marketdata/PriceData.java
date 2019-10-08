package hu.kits.investments.domain.marketdata;

import java.time.LocalDate;

import hu.kits.investments.domain.Asset;

public class PriceData {

    public final Asset asset;
    
    public final LocalDate date;
    
    public final double price;

    public PriceData(Asset asset, LocalDate date, double price) {
        this.asset = asset;
        this.date = date;
        this.price = price;
    }
    
    @Override
    public String toString() {
        return asset + " " +  date + ": " + price;
    }
    
}
