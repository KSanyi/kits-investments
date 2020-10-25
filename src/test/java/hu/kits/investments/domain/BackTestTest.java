package hu.kits.investments.domain;

import static java.time.LocalDate.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.investment.Allocation;
import hu.kits.investments.domain.investment.strategy.BuyAndHold;
import hu.kits.investments.domain.investment.strategy.InvestmentStrategy;
import hu.kits.investments.domain.investment.strategy.NeverInvest;
import hu.kits.investments.domain.marketdata.PriceHistory;

public class BackTestTest {

    private final Asset asset1 = new Asset("AAPL");
    private final Asset asset2 = new Asset("BAX");
    
    private final PriceHistory priceHistory = new PriceHistory(Map.of(
            asset1, Map.of(of(2000, 1, 1), 100.0,
                           of(2000, 1, 2), 110.0,
                           of(2000, 1, 3), 115.0,
                           of(2000, 1, 4), 120.0,
                           of(2000, 1, 5), 115.0,
                           of(2000, 1, 6), 110.0,
                           of(2000, 1, 7), 120.0,
                           of(2000, 1, 8), 115.0),
            asset2, Map.of(of(2000, 1, 1), 40.0,
                           of(2000, 1, 2), 35.0,
                           of(2000, 1, 3), 36.0,
                           of(2000, 1, 4), 32.0,
                           of(2000, 1, 5), 35.0,
                           of(2000, 1, 6), 36.0,
                           of(2000, 1, 7), 33.0,
                           of(2000, 1, 8), 30.0)));
    
    @Test
    void buyAndHoldApple() {
        
        BackTester backTester = new BackTester(priceHistory);
        
        DateRange dateRange = new DateRange(of(2000, 1, 1), of(2000, 1, 7));
        
        InvestmentStrategy strategy = new BuyAndHold(new Allocation(Map.of(asset1, 100)));
        
        var entry = backTester.run(strategy, dateRange).end();
        
        assertEquals(1_150_000, entry.value());
    }
    
    @Test
    void buyAndHoldBalancedPortfolio() {
        
        BackTester backTester = new BackTester(priceHistory);
        
        DateRange dateRange = new DateRange(of(2000, 1, 1), of(2000, 1, 7));
        
        InvestmentStrategy strategy = new BuyAndHold(new Allocation(Map.of(asset1, 50, asset2, 50)));
        
        var entry = backTester.run(strategy, dateRange).end();
        
        assertEquals(950_000, entry.value());
    }
    
    @Test
    void neverInvest() {
        
        BackTester backTester = new BackTester(priceHistory);
        
        DateRange dateRange = new DateRange(of(2000, 1, 1), of(2000, 1, 7));
        
        InvestmentStrategy strategy = new NeverInvest();
        
        var entry = backTester.run(strategy, dateRange).end();
        
        assertEquals(1_000_000, entry.value());
    }
    
}
