package hu.kits.investments.domain.investment.strategy;

import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;

import hu.kits.investments.domain.marketdata.PriceHistory;
import hu.kits.investments.domain.portfolio.PortfolioSnapshot;
import hu.kits.investments.domain.portfolio.TradeOrder;

public class NeverInvest implements InvestmentStrategy {

    @Override
    public List<TradeOrder> start(PriceHistory priceHistory, LocalDate startDate, int cash) {
        return emptyList();
    }

    @Override
    public List<TradeOrder> createTradeOrders(PortfolioSnapshot portfolio, PriceHistory priceHistory, LocalDate date) {
        return emptyList();
    }

}
