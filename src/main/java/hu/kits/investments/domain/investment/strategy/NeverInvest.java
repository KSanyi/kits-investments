package hu.kits.investments.domain.investment.strategy;

import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;

import hu.kits.investments.domain.PortfolioSnapshot;
import hu.kits.investments.domain.TradeOrder;
import hu.kits.investments.domain.investment.InvestmentStrategy;
import hu.kits.investments.domain.marketdata.PriceHistory;

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
