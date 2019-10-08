package hu.kits.investments;

import static java.time.LocalDate.of;

import java.net.URISyntaxException;
import java.time.LocalDate;

import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.marketdata.PriceDataRepository;
import hu.kits.investments.domain.marketdata.PriceDataService;
import hu.kits.investments.domain.marketdata.PriceDataSource;
import hu.kits.investments.domain.marketdata.PriceHistory;
import hu.kits.investments.domain.math.YieldCorrelationMatrix;
import hu.kits.investments.infrastructure.database.PriceDataJdbiRepository;
import hu.kits.investments.infrastructure.marketdata.yahoo.YahooPriceDataSource;

public class Main {

    public static void main(String[] args) throws Exception {
     
        PriceDataSource priceDataSource = new YahooPriceDataSource();
        
        DataSource dataSource = createDataSource();
        PriceDataRepository priceDataRepository = new PriceDataJdbiRepository(dataSource);
        
        PriceDataService priceDataService = new PriceDataService(priceDataSource, priceDataRepository);
        
        PriceHistory priceHistory = priceDataService.getPriceHistory().in(new DateRange(of(2010, 9, 9), of(2018,12,31)));
        
        System.out.println(YieldCorrelationMatrix.create(priceHistory));
        
        /*
        
        BackTester backTester = new BackTester(priceHistory);
        
        Asset asset1 = new Asset("CMS");
        Asset asset2 = new Asset("WEC");
        
        DateRange dateRange = new DateRange(of(2010, 1, 1), of(2018,12,31));
        
        InvestmentStrategy strategy = new BuyAndHold(new Allocation(Map.of(asset1, 50, asset2, 50)));
        InvestmentStats investmentStats = backTester.run(strategy, dateRange);
        
        System.out.println(investmentStats);
        
        /*
        
        List<Allocation> allocations = AllocationCreator.createAllocations(List.of(asset1, asset2), 10);
        
        for(Allocation allocation : allocations) {
            InvestmentStrategy strategy = new BuyAndHold(allocation);
            InvestmentStats investmentStats = backTester.run(strategy, dateRange);
            System.out.println(strategy + " result: " + investmentStats);
        }
        
        /*
        for(String ticker : Files.readAllLines(Paths.get("./data/SP_500_tickers.txt"))) {
            priceDataService.fetchAndSavePriceData(ticker, LocalDate.of(2010, 1, 1));
        }
        PriceHistory priceHistory = priceDataRepository.getPriceHistory();
        
        System.out.println(priceHistory.findPrice("AAPL", LocalDate.of(2019, 7, 14)));
        System.out.println(priceHistory.findPrice("LUV", LocalDate.of(2009, 7, 14)));
        System.out.println(priceHistory.findPrice("NWSA", LocalDate.of(2014, 7, 14)));
        System.out.println(priceHistory.findPrice("PPG", LocalDate.of(2011, 7, 14)));
        */
    }
    
    private static DataSource createDataSource() throws URISyntaxException {
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
