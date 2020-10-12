package hu.kits.investments.domain.portfolio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import hu.kits.investments.domain.Asset;

public class TradeOrderTest {

    private final Asset AAPL = new Asset("AAPL");
    
    @Test
    void test() {
        TradeOrder tradeOrder = new TradeOrder(LocalDate.parse("2020-09-01"), AAPL, 2, 300);
        assertEquals(600, tradeOrder.value());
    }

}
