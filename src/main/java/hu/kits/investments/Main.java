package hu.kits.investments;

import static java.time.LocalDate.of;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.cj.jdbc.MysqlDataSource;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.BackTester;
import hu.kits.investments.domain.asset.Asset;
import hu.kits.investments.domain.asset.AssetRepository;
import hu.kits.investments.domain.asset.Assets;
import hu.kits.investments.domain.investment.Allocation;
import hu.kits.investments.domain.investment.strategy.BuyAndHold;
import hu.kits.investments.domain.investment.strategy.ConstantAllocation;
import hu.kits.investments.domain.investment.strategy.InvestmentStrategy;
import hu.kits.investments.domain.marketdata.PriceDataRepository;
import hu.kits.investments.domain.marketdata.PriceDataService;
import hu.kits.investments.domain.marketdata.PriceDataSource;
import hu.kits.investments.domain.marketdata.PriceHistory;
import hu.kits.investments.domain.marketdata.fx.FXRateRepository;
import hu.kits.investments.domain.math.CorrelationMatrix;
import hu.kits.investments.domain.optimization.AllocationCreator;
import hu.kits.investments.domain.portfolio.PortfolioStats;
import hu.kits.investments.infrastructure.database.AssetJdbiRepository;
import hu.kits.investments.infrastructure.database.FXRateJdbcRepository;
import hu.kits.investments.infrastructure.database.PriceDataJdbiRepository;
import hu.kits.investments.infrastructure.marketdata.yahoo.YahooPriceDataSource;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private static final PriceDataSource priceDataSource = new YahooPriceDataSource();
    private static final DataSource dataSource = createDataSource();
    private static final PriceDataRepository priceDataRepository = new PriceDataJdbiRepository(dataSource);
    private static final AssetRepository assetRepository = new AssetJdbiRepository(dataSource);
    private static final Assets assets = assetRepository.loadAssets();
    private static final FXRateRepository fxRateRepository = new FXRateJdbcRepository(dataSource);
    private static final PriceDataService priceDataService = new PriceDataService(priceDataSource, priceDataRepository, fxRateRepository);
    
    public static void main(String[] args) throws Exception {

        //showCorrelationMatrix();
        //runBacktest1();
        Asset voo = assets.findByTicker("VOO");
        priceDataService.fetchAndSavePriceData(voo, LocalDate.of(2019, 7, 27));
        
        //runBacktest1();
        
        runBacktest2();
    }
    
    private static void fetchPriceData() throws IOException {
        for(String ticker : Files.readAllLines(Paths.get("./data/SP_500_tickers.txt"))) {
            Asset asset = assets.findByTicker(ticker);
            priceDataService.fetchAndSavePriceData(asset, LocalDate.of(2019, 7, 10));
        }
    }
    
    private static void showCorrelationMatrix() {
        
        Asset goog = assets.findByTicker("GOOG");
        Asset nflx = assets.findByTicker("NFLX");
        Asset msft = assets.findByTicker("MSFT");
        Asset aapl = assets.findByTicker("NFLX");
        Asset cof = assets.findByTicker("NFLX");
        
        Assets assets = Assets.of(goog, nflx, msft, aapl, cof);
        PriceHistory priceHistory = priceDataService.getPriceHistory(assets).in(new DateRange(LocalDate.of(2010, 1, 1), LocalDate.of(2020,1,1)));
        
        System.out.println(CorrelationMatrix.create(priceHistory));
    }
    
    private static void runBacktest1() {
        
        Asset asset1 = assets.findByTicker("GOOG");
        Asset asset2 = assets.findByTicker("NFLX");
        
        PriceHistory priceHistory = priceDataService.getPriceHistory(Assets.of(asset1, asset2))
                .in(new DateRange(of(2010, 1, 1), of(2018,12,31)));
        
        BackTester backTester = new BackTester(priceHistory);
        
        DateRange dateRange = new DateRange(of(2010, 1, 4), of(2018,12,31));
        
        InvestmentStrategy buyAndHoldStrategy = new BuyAndHold(new Allocation(Map.of(asset1, 50, asset2, 50)));
        PortfolioStats buyAndHoldPortfolioStats = backTester.run(buyAndHoldStrategy, dateRange);
        System.out.println("Buy and hold");
        System.out.println(buyAndHoldPortfolioStats);
        
        System.out.println("");
        
        ConstantAllocation constantAllocationStrategy = new ConstantAllocation(new Allocation(Map.of(asset1, 50, asset2, 50)), 6);
        PortfolioStats constantAllocationPortfolioStats = backTester.run(constantAllocationStrategy, dateRange);
        System.out.println("Constant allocation");
        System.out.println(constantAllocationPortfolioStats);
    }
    
    private static void runBacktest2() {
        
        Asset asset1 = assets.findByTicker("GOOG");
        Asset asset2 = assets.findByTicker("NFLX");
        
        PriceHistory priceHistory = priceDataService.getPriceHistory(Assets.of(asset1, asset2)).in(new DateRange(of(2010, 1, 1), of(2018,12,31)));
        
        BackTester backTester = new BackTester(priceHistory);
        
        List<Allocation> allocations = AllocationCreator.createAllocations(List.of(asset1, asset2), 10);
        
        DateRange dateRange = new DateRange(of(2010, 1, 4), of(2018,12,31));
        
        for(Allocation allocation : allocations) {
            InvestmentStrategy strategy = new BuyAndHold(allocation);
            PortfolioStats portfolioStats = backTester.run(strategy, dateRange);
            System.out.println(strategy + " result: " + portfolioStats);
        }
    }
    
    private static DataSource createDataSource() {
        String username = "root";
        String password = "Alma1234";
        String jdbcUrl = "jdbc:mysql://localhost/kits-investments?autoReconnect=true&useSSL=false"; 
        
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(jdbcUrl);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }
    
}
