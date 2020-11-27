package hu.kits.investments;

import static java.time.LocalDate.parse;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.asset.Asset;
import hu.kits.investments.domain.asset.AssetRepository;
import hu.kits.investments.domain.asset.Assets;
import hu.kits.investments.domain.marketdata.PriceDataRepository;
import hu.kits.investments.domain.marketdata.PriceHistory;
import hu.kits.investments.domain.marketdata.fx.FXRates;
import hu.kits.investments.domain.marketdata.fx.FXService;
import hu.kits.investments.domain.math.MathUtil;
import hu.kits.investments.domain.portfolio.Portfolio;
import hu.kits.investments.domain.portfolio.PortfolioSnapshot;
import hu.kits.investments.domain.portfolio.PortfolioStats;
import hu.kits.investments.domain.portfolio.PortfolioStatsCreator;
import hu.kits.investments.domain.portfolio.PortfolioValueSnapshot;
import hu.kits.investments.infrastructure.database.AssetJdbiRepository;
import hu.kits.investments.infrastructure.database.FXRateJdbcRepository;
import hu.kits.investments.infrastructure.database.PriceDataJdbiRepository;
import hu.kits.investments.infrastructure.marketdata.fx.NapiArfolyamService;

public class OrsiAlapok {

    private static final DataSource dataSource = createDataSource();
    private static final PriceDataRepository priceDataRepository = new PriceDataJdbiRepository(dataSource);
    private static final AssetRepository assetRepository = new AssetJdbiRepository(dataSource);
    private static final Assets assets = assetRepository.loadAssets();
    private static final FXService fxService = new FXService(new FXRateJdbcRepository(dataSource), new NapiArfolyamService());
    private static final FXRates fxRates = fxService.getFXRates();
    
    private static final LocalDate REF_DATE = parse("2020-11-16");
    
    public static void main(String[] args) {
        
        fxService.downloadDailyFxRates();
        
        //run("AEG_N_KOT", parse("2020-07-02"), 1_924_974);
        //run("BF_MON_FEJ", parse("2020-07-03"), 2_970_295);
        //run("BUD_AKT_AL", parse("2019-12-31"), 7_649_840);
        //run("BUD_KONTR", parse("2020-07-08"), 6_965_174);
        //run("BUD_ALLAM", parse("2020-01-13"), 4_990_013);
        
        run();
        System.out.println();
        runVOO();
    }
    
    private static void run() {
        
        //Asset asset1 = assets.findByTicker("BUD_KOTV");
        Asset asset2 = assets.findByTicker("BUD_ALLAM");
        Asset asset3 = assets.findByTicker("AEG_N_KOT");
        Asset asset4 = assets.findByTicker("BUD_AKT_AL");
        Asset asset5 = assets.findByTicker("BF_MON_FEJ");
        Asset asset6 = assets.findByTicker("BUD_KONTR");
        
        LocalDate start = parse("2019-12-31");
        PriceHistory priceHistory = priceDataRepository.getPriceHistory(Assets.of(asset2, asset3, asset4, asset5, asset6))
                .in(new DateRange(start, REF_DATE));
        
        Portfolio portfolio = new Portfolio();
        //portfolio.deposit(parse("2019-02-11"), 8_500_000);
        //buy(portfolio, asset1, parse("2019-02-14"), 1_032_069, 8_473_596);
        
        portfolio.deposit(parse("2019-12-31"), 14_901_492);
        buy(portfolio, asset4, parse("2019-12-31"), 13_025_780 , 14_901_492);
        
        portfolio.deposit(parse("2020-01-13"), 4_990_013);
        buy(portfolio, asset2, parse("2020-01-13"), 589_530 , 4_990_013);
        
        portfolio.deposit(parse("2020-07-06"), 1_924_974);
        buy(portfolio, asset3, parse("2020-07-06"), 854_585 , 1_924_974);
        
        sell(portfolio, asset4, parse("2020-07-07"), 6338857 , 7_251_652);
        
        portfolio.deposit(parse("2020-07-07"), 2_970_295);
        buy(portfolio, asset5, parse("2020-07-07"), 1_678_150 , 2_970_295);
        
        buy(portfolio, asset6, parse("2020-07-10"), 4_283_119 , 6_965_174);
        
        PortfolioStats portfolioStats = PortfolioStatsCreator.createPortfolioStats(portfolio, priceHistory);
        PortfolioSnapshot portfolioSnapshot = portfolio.createSnapshotAt(REF_DATE);
        PortfolioValueSnapshot portfolioValueSnapshot = portfolioSnapshot.valuate(priceHistory.assetPricesAt(REF_DATE));
        
        System.out.println(portfolioSnapshot);
        System.out.println(portfolioValueSnapshot);
        System.out.println(portfolioStats);
    }
    
    private static void runVOO() {
        
        Asset voo = assets.findByTicker("VOO");
        
        LocalDate start = parse("2019-12-31");
        PriceHistory priceHistory = priceDataRepository.getPriceHistory(Assets.of(voo))
                .in(new DateRange(start, REF_DATE));
        
        Portfolio portfolio = new Portfolio();
        
        portfolio.deposit(parse("2019-12-31"), 14_901_492);
        buyForAmount(portfolio, voo, parse("2019-12-31"), 14_901_492, priceHistory);
        
        portfolio.deposit(parse("2020-01-13"), 4_990_013);
        buyForAmount(portfolio, voo, parse("2020-01-13"), 4_990_013, priceHistory);
        
        portfolio.deposit(parse("2020-07-06"), 1_924_974);
        buyForAmount(portfolio, voo, parse("2020-07-06"), 1_924_974, priceHistory);
        
        sellForAmount(portfolio, voo, parse("2020-07-07"), 7_251_652, priceHistory);
        
        portfolio.deposit(parse("2020-07-07"), 2_970_295);
        buyForAmount(portfolio, voo, parse("2020-07-07"), 2_970_295, priceHistory);
        
        buyForAmount(portfolio, voo, parse("2020-07-10"), 6_965_174, priceHistory);
        
        PortfolioStats portfolioStats = PortfolioStatsCreator.createPortfolioStats(portfolio, priceHistory);
        PortfolioSnapshot portfolioSnapshot = portfolio.createSnapshotAt(REF_DATE);
        PortfolioValueSnapshot portfolioValueSnapshot = portfolioSnapshot.valuate(priceHistory.assetPricesAt(REF_DATE));
        
        System.out.println(portfolioSnapshot);
        System.out.println(portfolioValueSnapshot);
        System.out.println(portfolioStats);
    }
    
    private static void buy(Portfolio portfolio, Asset asset, LocalDate date, int quantity, int amount) {
        BigDecimal price = new BigDecimal(amount / (double)quantity);
        portfolio.buy(date, asset, quantity, price);
    }
    
    private static void buyForAmount(Portfolio portfolio, Asset asset, LocalDate date, int amount, PriceHistory priceHistory) {
        BigDecimal price = priceHistory.getPrice(asset, date);
        int quantity = MathUtil.divideFloor(amount, price).intValue();
        portfolio.buy(date, asset, quantity, price);
    }
    
    private static void sell(Portfolio portfolio, Asset asset, LocalDate date, int quantity, int amount) {
        BigDecimal price = new BigDecimal(amount / (double)quantity);
        portfolio.sell(date, asset, quantity, price);
    }
    
    private static void sellForAmount(Portfolio portfolio, Asset asset, LocalDate date, int amount, PriceHistory priceHistory) {
        BigDecimal price = priceHistory.getPrice(asset, date);
        int quantity = MathUtil.divideCeil(amount, price).intValue();
        portfolio.sell(date, asset, quantity, price);
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
