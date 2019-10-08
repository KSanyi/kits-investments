package hu.kits.investments.domain;

import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.kits.investments.common.DateRange;

public class InvestmentStats {

    public final int startValue;
    
    public final int endValue;
    
    public final double yield;
    
    public final double yieldPerYear;

    public InvestmentStats(DateRange dateRange, int startValue, int endValue) {
        this.startValue = startValue;
        this.endValue = endValue;
        yield = (endValue - startValue) / (double)startValue;
        int days = (int)ChronoUnit.DAYS.between(dateRange.from, dateRange.to.plusDays(1));
        yieldPerYear = Math.pow(1 + yield, 365.0 / days) - 1;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
