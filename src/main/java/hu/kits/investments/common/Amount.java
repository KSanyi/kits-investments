package hu.kits.investments.common;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import hu.kits.investments.domain.asset.Currency;

public record Amount(Currency currency, BigDecimal value) {

    private static final DecimalFormat FORMAT;
    private static final DecimalFormat FORMAT_00;
    
    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        decimalFormatSymbols.setGroupingSeparator(' ');
        FORMAT = new DecimalFormat("###,###", decimalFormatSymbols);
        FORMAT.setParseBigDecimal(true);
        FORMAT_00 = new DecimalFormat("###,##0.00", decimalFormatSymbols);
        FORMAT_00.setParseBigDecimal(true);
    }
    
    public Amount(Currency currency, int main, int sub) {
        this(currency, new BigDecimal(main + "." + (sub > 9 ? sub : "0" + sub)));
    }
    
    public Amount(Currency currency, int main) {
        this(currency, main, 0);
    }
    
    public static Amount parse(Currency currency, String valueString) {
        try {
            return new Amount(currency, (BigDecimal)FORMAT_00.parse(valueString.replaceAll("\\,", "\\.")));
        } catch (ParseException e) {
            throw new RuntimeException("Could not format " + valueString);
        }
    }
    
    public Amount add(Amount other) {
        currencyCheck(other);
        return new Amount(currency, value.add(other.value));
    }
    
    public Amount subtract(Amount other) {
        currencyCheck(other);
        return new Amount(currency, value.subtract(other.value));
    }
    
    public Amount multiply(int quantity) {
        return multiply(BigDecimal.valueOf(quantity));
    }
    
    public Amount multiply(BigDecimal multiplier) {
        return new Amount(currency, value.multiply(multiplier));
    }
    
    private void currencyCheck(Amount other) {
        if(currency  != other.currency) throw new IllegalArgumentException("Adding different currencies: " + currency + ", " + other.currency);
    }
    
    @Override
    public String toString() {
        return toStringNoCurrency()  + " " + currency.sign;
    }
    
    public String toString2Decimals() {
        return FORMAT_00.format(value)  + " " + currency.sign;
    }
    
    public String toStringNoCurrency() {
        DecimalFormat format = currency == Currency.HUF ? FORMAT : FORMAT_00;
        return format.format(value);   
    }
    
    @Override
    public boolean equals(Object other) {
        if(other == this) return true;
        if(other == null || !(other instanceof Amount)) return false;
        Amount otherAmount = (Amount) other;
        return otherAmount.currency == currency && otherAmount.value.compareTo(value) == 0;
    }
    
    public Amount negate() {
        return new Amount(currency, value.negate());
    }

    public boolean isNegative() {
        return value.signum() == -1;
    }
    
    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }
    
    public static Amount HUF(int main) {
        return new Amount(Currency.HUF, main);
    }
    
    public static Amount EUR(int main) {
        return new Amount(Currency.EUR, main);
    }
    
    public static Amount ZERO(Currency currency) {
        return new Amount(currency, BigDecimal.ZERO);
    }
    
    public static Map<Currency, Amount> sumAmountsByCurrency(List<Amount> amountList) {
        
        return amountList.stream()
            .collect(groupingBy(amount -> amount.currency, reducing(Amount::add)))
            .entrySet().stream().collect(toMap(Entry::getKey, e -> e.getValue().get()));
    }
    
    public static String createSummary(List<Amount> amountList) {
        
        return sumAmountsByCurrency(amountList).values().stream()
                .map(Amount::toString)
                .collect(joining(", "));
    }

}
