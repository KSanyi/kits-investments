package hu.kits.investments.domain.portfolio;

import static hu.kits.investments.domain.TestAssets.AAPL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import hu.kits.investments.domain.portfolio.TradeOrder.Side;

public class PositionTest {

    @Test
    void updateTest() {
        Position position = new Position(AAPL, 1);
        position = position.update(new TradeOrder(LocalDate.parse("2020-09-01"), AAPL, Side.BUY, 2, new BigDecimal(300)));
        
        assertEquals(3, position.quantity());
    }
    
    @Test
    void combineTest() {
        Position combindedPosition = new Position(AAPL, 1).combine(new Position(AAPL, 2));
        
        assertEquals(3, combindedPosition.quantity());
    }
    
    @Test
    void aggregateTest() {
        Position position = Position.aggregate(AAPL, List.of(
                new TradeOrder(LocalDate.parse("2020-09-01"), AAPL, Side.BUY,  2, new BigDecimal(300)),
                new TradeOrder(LocalDate.parse("2020-09-02"), AAPL, Side.BUY,  3, new BigDecimal(310)),
                new TradeOrder(LocalDate.parse("2020-09-03"), AAPL, Side.SELL, 4, new BigDecimal(290)),
                new TradeOrder(LocalDate.parse("2020-09-04"), AAPL, Side.BUY,  1, new BigDecimal(300))));
        
        assertEquals(2, position.quantity());
    }

}
