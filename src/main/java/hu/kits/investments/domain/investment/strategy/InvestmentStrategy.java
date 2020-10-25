package hu.kits.investments.domain.investment.strategy;

import java.time.LocalDate;
import java.util.List;

import hu.kits.investments.domain.marketdata.PriceHistory;
import hu.kits.investments.domain.portfolio.PortfolioSnapshot;
import hu.kits.investments.domain.portfolio.TradeOrder;

public interface InvestmentStrategy {

    List<TradeOrder> start(PriceHistory priceHistory, LocalDate startDate, int cash);
    
    List<TradeOrder> createTradeOrders(PortfolioSnapshot portfolio, PriceHistory priceHistory, LocalDate date);
     
}
