package hu.kits.investments.domain.asset;

public record Asset(String ticker, String name, AssetClass assetClass, String isin, Currency currency) {

    public static enum AssetClass {
        EQUITY, MUTUAL_FUND, ETF, INDEX, CASH
    }
    
    public static Asset fromCurrency(Currency currency) {
        return new Asset(currency.name(), currency.name(), AssetClass.CASH, "", Currency.HUF);
    }
    
}
