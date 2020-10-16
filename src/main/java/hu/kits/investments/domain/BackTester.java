package hu.kits.investments.domain;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;

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
        
        //logger.info("Starting back test on {} with {} USD", dateRange.from, startingMoney);
        
        List<TradeOrder> initialTradeOrders = strategy.start(priceHistory, dateRange.from, startingMoney);
        execureTradeOrders(portfolio, initialTradeOrders);
        
        for(LocalDate date : dateRange) {
            PortfolioSnapshot portfolioSnapshot = portfolio.createSnapshot();
            List<TradeOrder> tradeOrders = strategy.createTradeOrders(portfolioSnapshot, priceHistory, date);
            execureTradeOrders(portfolio, tradeOrders);
            
            if(date.getMonthValue() == 1 && date.getDayOfMonth() == 1) {
                //logger.info("Portfolio on {}: {}", date, portfolio);
            }
        }
        
        PortfolioStats portfolioStats = PortfolioStatsCreator.createPortfolioStats(portfolio, priceHistory);
        
        return portfolioStats;
    }

    private static void execureTradeOrders(Portfolio portfolio, List<TradeOrder> tradeOrders) {
        
        for(TradeOrder tradeOrder : tradeOrders) {
            portfolio.buy(tradeOrder.date(), tradeOrder.asset(), tradeOrder.quantity(), tradeOrder.unitPrice());
        }
    }
    
}
