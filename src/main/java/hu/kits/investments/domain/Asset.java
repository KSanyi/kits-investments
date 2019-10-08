package hu.kits.investments.domain;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class Asset {

    public final String ticker;

    public Asset(String ticker) {
        this.ticker = ticker;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(ticker);
    }
    
    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
    
    @Override
    public String toString() {
        return ticker;
    }
    
}
