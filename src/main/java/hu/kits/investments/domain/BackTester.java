package hu.kits.investments.domain;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.investment.strategy.InvestmentStrategy;
import hu.kits.investments.domain.marketdata.PriceHistory;
import hu.kits.investments.domain.portfolio.Portfolio;
import hu.kits.investments.domain.portfolio.PortfolioSnapshot;
import hu.kits.investments.domain.portfolio.PortfolioStats;
import hu.kits.investments.domain.portfolio.PortfolioStatsCreator;
import hu.kits.investments.domain.portfolio.TradeOrder;
import hu.kits.investments.domain.portfolio.TradeOrder.Side;

public class BackTester {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final PriceHistory priceHistory;
    
    public BackTester(PriceHistory priceHistory) {
        this.priceHistory = priceHistory;
    }

    public PortfolioStats run(InvestmentStrategy strategy, DateRange dateRange) {
        
        int startingMoney = 1_000_000;
        Portfolio portfolio = new Portfolio();
        portfolio.deposit(dateRange.from, 1_000_000);
        
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
        
        List<TradeOrder> sellOrders = tradeOrders.stream()
                .filter(order -> order.side() == Side.SELL)
                .filter(order -> order.quantity() > 0)
                .toList();
        
        for(TradeOrder tradeOrder : sellOrders) {
            portfolio.sell(tradeOrder.date(), tradeOrder.asset(), tradeOrder.quantity(), tradeOrder.unitPrice());
        }
        
        List<TradeOrder> buyOrders = tradeOrders.stream()
                .filter(order -> order.side() == Side.BUY)
                .filter(order -> order.quantity() > 0)
                .toList();
        
        
        for(TradeOrder tradeOrder : buyOrders) {
            portfolio.buy(tradeOrder.date(), tradeOrder.asset(), tradeOrder.quantity(), tradeOrder.unitPrice());
        }
        
    }
    
}
