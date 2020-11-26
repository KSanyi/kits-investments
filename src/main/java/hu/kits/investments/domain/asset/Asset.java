package hu.kits.investments.domain.asset;

public record Asset(String ticker, String name, AssetClass assetClass, String isin) {

    public static enum AssetClass {
        EQUITY, MUTUAL_FUND, ETF, INDEX
    }
    
}
