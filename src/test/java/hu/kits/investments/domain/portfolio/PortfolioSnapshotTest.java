package hu.kits.investments.domain.portfolio;

import static hu.kits.investments.domain.TestAssets.AAPL;
import static hu.kits.investments.domain.TestAssets.AMZN;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import hu.kits.investments.domain.marketdata.AssetPrices;

public class PortfolioSnapshotTest {

    private final AssetPrices assetPrices1 = new AssetPrices(Map.of(AAPL, 200.0, AMZN, 300.0));
    private final AssetPrices assetPrices2 = new AssetPrices(Map.of(AAPL, 250.0, AMZN, 250.0));
    
    @Test
    void test() {
        PortfolioSnapshot portfolioSnapshot = new PortfolioSnapshot(Map.of(AAPL, 1, AMZN, 2), 1_000);
        
        assertEquals(1_800, portfolioSnapshot.portfolioValue(assetPrices1));
        assertEquals(1_750, portfolioSnapshot.portfolioValue(assetPrices2));
    }

}
