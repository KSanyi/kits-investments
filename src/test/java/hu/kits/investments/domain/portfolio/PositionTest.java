package hu.kits.investments.domain.portfolio;

import static hu.kits.investments.domain.TestAssets.AAPL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PositionTest {

    @Test
    void updateTest() {
        Position position = new Position(AAPL, 1);
        position = position.update(new TradeOrder(LocalDate.parse("2020-09-01"), AAPL, 2, 300));
        
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
                new TradeOrder(LocalDate.parse("2020-09-01"), AAPL,  2, 300),
                new TradeOrder(LocalDate.parse("2020-09-02"), AAPL,  3, 310),
                new TradeOrder(LocalDate.parse("2020-09-03"), AAPL, -4, 290),
                new TradeOrder(LocalDate.parse("2020-09-04"), AAPL,  1, 300)));
        
        assertEquals(2, position.quantity());
    }

}
