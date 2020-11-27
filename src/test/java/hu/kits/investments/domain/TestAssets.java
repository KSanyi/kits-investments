package hu.kits.investments.domain;

import static hu.kits.investments.domain.asset.Currency.USD;

import hu.kits.investments.domain.asset.Asset;
import hu.kits.investments.domain.asset.Asset.AssetClass;

public class TestAssets {

    public static final Asset AAPL = new Asset("AAPL", "", AssetClass.EQUITY, "", USD);
    public static final Asset AMZN = new Asset("AMZN", "", AssetClass.EQUITY, "", USD);
    public static final Asset BAX = new Asset("BAX", "", AssetClass.EQUITY, "", USD);
    public static final Asset GOOG = new Asset("GOOG", "", AssetClass.EQUITY, "", USD);
    
}
