package hu.kits.investments.domain.portfolio;

import static hu.kits.investments.domain.TestAssets.AAPL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import hu.kits.investments.domain.portfolio.TradeOrder.Side;

public class TradeOrderTest {

    @Test
    void test() {
        TradeOrder tradeOrder = new TradeOrder(LocalDate.parse("2020-09-01"), AAPL, Side.BUY, 2, new BigDecimal(300));
        assertEquals(600, tradeOrder.value());
    }

}
