package hu.kits.investments.domain;

import static hu.kits.investments.domain.TestAssets.AAPL;
import static hu.kits.investments.domain.TestAssets.BAX;
import static java.time.LocalDate.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.investment.Allocation;
import hu.kits.investments.domain.investment.strategy.BuyAndHold;
import hu.kits.investments.domain.investment.strategy.InvestmentStrategy;
import hu.kits.investments.domain.investment.strategy.NeverInvest;
import hu.kits.investments.domain.marketdata.PriceHistory;

public class BackTestTest {

    private final PriceHistory priceHistory = new PriceHistory(Map.of(
            AAPL, Map.of(of(2000, 1, 1), new BigDecimal(100.0),
                           of(2000, 1, 2), new BigDecimal(110.0),
                           of(2000, 1, 3), new BigDecimal(115.0),
                           of(2000, 1, 4), new BigDecimal(120.0),
                           of(2000, 1, 5), new BigDecimal(115.0),
                           of(2000, 1, 6), new BigDecimal(110.0),
                           of(2000, 1, 7), new BigDecimal(120.0),
                           of(2000, 1, 8), new BigDecimal(115.0)),
            BAX, Map.of(of(2000, 1, 1), new BigDecimal(40.0),
                           of(2000, 1, 2), new BigDecimal(35.0),
                           of(2000, 1, 3), new BigDecimal(36.0),
                           of(2000, 1, 4), new BigDecimal(32.0),
                           of(2000, 1, 5), new BigDecimal(35.0),
                           of(2000, 1, 6), new BigDecimal(36.0),
                           of(2000, 1, 7), new BigDecimal(33.0),
                           of(2000, 1, 8), new BigDecimal(30.0))));
    
    @Test
    void buyAndHoldApple() {
        
        BackTester backTester = new BackTester(priceHistory);
        
        DateRange dateRange = new DateRange(of(2000, 1, 1), of(2000, 1, 7));
        
        InvestmentStrategy strategy = new BuyAndHold(new Allocation(Map.of(AAPL, 100)));
        
        var entry = backTester.run(strategy, dateRange).end();
        
        assertEquals(1_150_000, entry.value());
    }
    
    @Test
    void buyAndHoldBalancedPortfolio() {
        
        BackTester backTester = new BackTester(priceHistory);
        
        DateRange dateRange = new DateRange(of(2000, 1, 1), of(2000, 1, 7));
        
        InvestmentStrategy strategy = new BuyAndHold(new Allocation(Map.of(AAPL, 50, BAX, 50)));
        
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
