package hu.kits.investments.domain;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.investment.InvestmentStrategy;
import hu.kits.investments.domain.marketdata.PriceHistory;
import hu.kits.investments.domain.portfolio.Portfolio;
import hu.kits.investments.domain.portfolio.PortfolioSnapshot;
import hu.kits.investments.domain.portfolio.PortfolioStats;
import hu.kits.investments.domain.portfolio.PortfolioStatsCreator;
import hu.kits.investments.domain.portfolio.TradeOrder;

public class BackTester {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final PriceHistory priceHistory;
    
    public BackTester(PriceHistory priceHistory) {
        this.priceHistory = priceHistory;
    }

    public PortfolioStats run(InvestmentStrategy strategy, DateRange dateRange) {
        
        int startingMoney = 1_000_000;
        Portfolio portfolio = new Portfolio(startingMoney);
        
        logger.info("Starting back test on {} with {} USD", dateRange.from, startingMoney);
        
        List<TradeOrder> initialTradeOrders = strategy.start(priceHistory, dateRange.from, startingMoney);
        executeTradeOrders(portfolio, initialTradeOrders);
        
        for(LocalDate date : dateRange) {
            PortfolioSnapshot portfolioSnapshot = portfolio.createSnapshot();
            List<TradeOrder> tradeOrders = strategy.createTradeOrders(portfolioSnapshot, priceHistory, date);
            executeTradeOrders(portfolio, tradeOrders);
            
            if(date.getMonthValue() == 1 && date.getDayOfMonth() == 1) {
                logger.info("Portfolio on {}: {}", date, portfolioSnapshot.valuate(priceHistory.assetPricesAt(date)));
            }
        }
        
        PortfolioStats portfolioStats = PortfolioStatsCreator.createPortfolioStats(portfolio, priceHistory);
        
        return portfolioStats;
    }

    private static void executeTradeOrders(Portfolio portfolio, List<TradeOrder> tradeOrders) {
        
        // sell first
        List<TradeOrder> sortedTradeOrders = tradeOrders.stream().sorted(comparing(TradeOrder::quantity)).collect(toList());
        
        for(TradeOrder tradeOrder : sortedTradeOrders) {
            portfolio.buy(tradeOrder.date(), tradeOrder.asset(), tradeOrder.quantity(), tradeOrder.unitPrice());
        }
    }
    
}
