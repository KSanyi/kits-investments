package hu.kits.investments.domain.asset;

public enum Currency {

    EUR("\u20AC"), HUF("Ft"), USD("$"), CHF("Fr"), GBP("\u00A3");
    
    public final String sign;
    
    private Currency(String sign) {
        this.sign = sign;
    }
    
}
