package hu.kits.investments.domain.portfolio;

import static hu.kits.investments.domain.TestAssets.AAPL;
import static hu.kits.investments.domain.TestAssets.AMZN;
import static java.time.LocalDate.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PortfolioTest {

    @Test
    void buy() {
        Portfolio portfolio = new Portfolio();
        portfolio.deposit(parse("2020-08-30"), 3_000);
        
        Assertions.assertEquals(3_000, portfolio.cash());
        Assertions.assertEquals(0, portfolio.quantity(AAPL));
        
        portfolio.buy(parse("2020-09-01"), AAPL, 1, 500);
        portfolio.buy(parse("2020-09-02"), AAPL, 2, 550);
        
        Assertions.assertEquals(1_400, portfolio.cash());
        Assertions.assertEquals(3, portfolio.quantity(AAPL));
        
        Assertions.assertEquals(3_000, portfolio.cashAt(parse("2020-08-30")));
        Assertions.assertEquals(2_500, portfolio.cashAt(parse("2020-09-01")));
        Assertions.assertEquals(1_400, portfolio.cashAt(parse("2020-09-02")));
        
        Assertions.assertEquals(0, portfolio.quantityAt(AAPL, parse("2020-08-30")));
        Assertions.assertEquals(1, portfolio.quantityAt(AAPL, parse("2020-09-01")));
        Assertions.assertEquals(3, portfolio.quantityAt(AAPL, parse("2020-09-02")));
    }
    
    @Test
    void sell() {
        Portfolio portfolio = new Portfolio();
        portfolio.deposit(parse("2020-08-30"), 1_000);
        portfolio.buy(parse("2020-09-01"), AAPL, 2, 300);
        
        Assertions.assertEquals(400, portfolio.cash());
        Assertions.assertEquals(2, portfolio.quantity(AAPL));
        
        portfolio.sell(parse("2020-09-02"), AAPL, 1, 350);
        
        Assertions.assertEquals(750, portfolio.cash());
        Assertions.assertEquals(1, portfolio.quantity(AAPL));
        
        Assertions.assertEquals(1_000, portfolio.cashAt(parse("2020-08-30")));
        Assertions.assertEquals(  400, portfolio.cashAt(parse("2020-09-01")));
        Assertions.assertEquals(  750, portfolio.cashAt(parse("2020-09-02")));
        
        Assertions.assertEquals(0, portfolio.quantityAt(AAPL, parse("2020-08-30")));
        Assertions.assertEquals(2, portfolio.quantityAt(AAPL, parse("2020-09-01")));
        Assertions.assertEquals(1, portfolio.quantityAt(AAPL, parse("2020-09-02")));
    }
    
    @Test
    void buy2() {
        Portfolio portfolio = new Portfolio();
        portfolio.deposit(parse("2020-08-30"), 1_000);
        
        Assertions.assertEquals(1000, portfolio.cash());
        Assertions.assertEquals(0, portfolio.quantity(AAPL));
        
        portfolio.buy(parse("2020-09-01"), AAPL, 1, 500);
        portfolio.buy(parse("2020-09-01"), AMZN, 2, 200);
        
        Assertions.assertEquals(100, portfolio.cash());
        Assertions.assertEquals(1, portfolio.quantity(AAPL));
        Assertions.assertEquals(2, portfolio.quantity(AMZN));
    }
    
    @Test
    void buyTooMuch() {
        Portfolio portfolio = new Portfolio();
        portfolio.deposit(parse("2020-08-30"), 1_000);
        
        Assertions.assertThrows(IllegalStateException.class, () -> {
            portfolio.buy(parse("2020-09-01"), AAPL, 1, 1101);
        });
    }
    
    @Test
    void sellTooMuch() {
        Portfolio portfolio = new Portfolio();
        portfolio.deposit(parse("2020-09-01"), 1_000);
        portfolio.buy(parse("2020-09-01"), AAPL, 1, 500);
        
        Assertions.assertThrows(IllegalStateException.class, () -> {
            portfolio.sell(parse("2020-09-01"), AAPL, 2, 1000);
        });
    }
    
    @Test
    void snapshot() {
        Portfolio portfolio = new Portfolio();
        portfolio.deposit(parse("2020-08-30"), 10_000);
        
        portfolio.buy(parse("2020-09-01"), AAPL, 10, 500);
        portfolio.buy(parse("2020-09-01"), AMZN, 20, 200);

        portfolio.buy(parse("2020-09-02"), AMZN, -10, 300);
        portfolio.buy(parse("2020-09-02"), AAPL,   5, 400);
        
        assertEquals(new PortfolioSnapshot(Map.of(AAPL, 10, AMZN, 20), 1_000), portfolio.createSnapshotAt(parse("2020-09-01")));
        assertEquals(new PortfolioSnapshot(Map.of(AAPL, 15, AMZN, 10), 2_000), portfolio.createSnapshotAt(parse("2020-09-02")));
    }
    
}
