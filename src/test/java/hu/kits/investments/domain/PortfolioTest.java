package hu.kits.investments.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PortfolioTest {

    private final Asset AAPL = new Asset("AAPL");
    private final Asset AMZN = new Asset("AMZN");
    
    @Test
    void buy() {
        Portfolio portfolio = new Portfolio(1_000);
        
        Assertions.assertEquals(1000, portfolio.cash());
        Assertions.assertEquals(0, portfolio.quantity(AAPL));
        
        portfolio.buy(AAPL, 1, 500);
        
        Assertions.assertEquals(500, portfolio.cash());
        Assertions.assertEquals(1, portfolio.quantity(AAPL));
    }
    
    @Test
    void sell() {
        Portfolio portfolio = new Portfolio(1_000);
        portfolio.buy(AAPL, 2, 600);
        
        Assertions.assertEquals(400, portfolio.cash());
        Assertions.assertEquals(2, portfolio.quantity(AAPL));
        
        portfolio.sell(AAPL, 1, 350);
        
        Assertions.assertEquals(750, portfolio.cash());
        Assertions.assertEquals(1, portfolio.quantity(AAPL));
    }
    
    @Test
    void buy2() {
        Portfolio portfolio = new Portfolio(1_000);
        
        Assertions.assertEquals(1000, portfolio.cash());
        Assertions.assertEquals(0, portfolio.quantity(AAPL));
        
        portfolio.buy(AAPL, 1, 500);
        portfolio.buy(AMZN, 2, 500);
        
        Assertions.assertEquals(0, portfolio.cash());
        Assertions.assertEquals(1, portfolio.quantity(AAPL));
        Assertions.assertEquals(2, portfolio.quantity(AMZN));
    }
    
    @Test
    void buyTooMuch() {
        Portfolio portfolio = new Portfolio(1_000);
        
        Assertions.assertThrows(IllegalStateException.class, () -> {
            portfolio.buy(AAPL, 1, 1001);
        });
    }
    
    @Test
    void sellTooMuch() {
        Portfolio portfolio = new Portfolio(1_000);
        portfolio.buy(AAPL, 1, 500);
        
        Assertions.assertThrows(IllegalStateException.class, () -> {
            portfolio.sell(AAPL, 2, 1000);
        });
    }
    
}
