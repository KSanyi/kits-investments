package hu.kits.investments.domain.investment;

import java.time.LocalDate;
import java.util.List;

import hu.kits.investments.domain.PortfolioSnapshot;
import hu.kits.investments.domain.TradeOrder;
import hu.kits.investments.domain.marketdata.PriceHistory;

public interface InvestmentStrategy {

    List<TradeOrder> start(PriceHistory priceHistory, LocalDate startDate, int cash);
    
    List<TradeOrder> createTradeOrders(PortfolioSnapshot portfolio, PriceHistory priceHistory, LocalDate date);
     
}
