package hu.kits.investments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.Asset;
import hu.kits.investments.domain.BackTester;
import hu.kits.investments.domain.investment.Allocation;
import hu.kits.investments.domain.investment.InvestmentStrategy;
import hu.kits.investments.domain.investment.strategy.BuyAndHold;
import hu.kits.investments.domain.marketdata.PriceDataRepository;
import hu.kits.investments.domain.marketdata.PriceDataService;
import hu.kits.investments.domain.marketdata.PriceDataSource;
import hu.kits.investments.domain.marketdata.PriceHistory;
import hu.kits.investments.domain.math.YieldCorrelationMatrix;
import hu.kits.investments.domain.optimization.AllocationCreator;
import hu.kits.investments.domain.portfolio.PortfolioStats;
import hu.kits.investments.infrastructure.database.PriceDataJdbiRepository;
import hu.kits.investments.infrastructure.marketdata.yahoo.YahooPriceDataSource;

public class Main {

    private static PriceDataSource priceDataSource = new YahooPriceDataSource();
    private static DataSource dataSource = createDataSource();
    private static PriceDataRepository priceDataRepository = new PriceDataJdbiRepository(dataSource);
    
    private static PriceDataService priceDataService = new PriceDataService(priceDataSource, priceDataRepository);
    
    public static void main(String[] args) throws Exception {

        //showCorrelationMatrix();
        runBacktest2();
    }
    
    private static void fetchPriceData() throws IOException {
        for(String ticker : Files.readAllLines(Paths.get("./data/SP_500_tickers.txt"))) {
            priceDataService.fetchAndSavePriceData(ticker, LocalDate.of(2019, 7, 10));
        }
    }
    
    private static void showCorrelationMatrix() {
        PriceHistory priceHistory = priceDataService.getPriceHistory().in(new DateRange(LocalDate.of(2010, 1, 1), LocalDate.of(2020,1,1)));
        
        System.out.println(YieldCorrelationMatrix.create(priceHistory));
    }
    
    private static void runBacktest1() {
        PriceHistory priceHistory = priceDataService.getPriceHistory().in(new DateRange(LocalDate.of(2010, 1, 1), LocalDate.of(2018,12,31)));
        
        BackTester backTester = new BackTester(priceHistory);
        
        Asset asset1 = new Asset("CMS");
        Asset asset2 = new Asset("WEC");
        
        DateRange dateRange = new DateRange(LocalDate.of(2010, 1, 4), LocalDate.of(2018,12,31));
        
        InvestmentStrategy strategy = new BuyAndHold(new Allocation(Map.of(asset1, 50, asset2, 50)));
        PortfolioStats portfolioStats = backTester.run(strategy, dateRange);
        
        System.out.println(portfolioStats);
    }
    
    private static void runBacktest2() {
        
        PriceHistory priceHistory = priceDataService.getPriceHistory().in(new DateRange(LocalDate.of(2010, 1, 1), LocalDate.of(2018,12,31)));
        
        BackTester backTester = new BackTester(priceHistory);
        
        Asset asset1 = new Asset("CMS");
        Asset asset2 = new Asset("WEC");
        
        List<Allocation> allocations = AllocationCreator.createAllocations(List.of(asset1, asset2), 10);
        
        DateRange dateRange = new DateRange(LocalDate.of(2010, 1, 4), LocalDate.of(2018,12,31));
        
        for(Allocation allocation : allocations) {
            InvestmentStrategy strategy = new BuyAndHold(allocation);
            PortfolioStats portfolioStats = backTester.run(strategy, dateRange);
            System.out.println(strategy + " result: " + portfolioStats);
        }
    }
    
    private static DataSource createDataSource() {
        String username = "root";
        String password = "abcd1234";
        String jdbcUrl = "jdbc:mysql://localhost/kits-investments?autoReconnect=true&useSSL=false"; 
        
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(jdbcUrl);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }
    
}
