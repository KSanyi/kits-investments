package hu.kits.investments.infrastructure.database;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.investments.domain.asset.Asset;
import hu.kits.investments.domain.asset.Assets;
import hu.kits.investments.domain.marketdata.PriceData;
import hu.kits.investments.domain.marketdata.PriceDataRepository;
import hu.kits.investments.domain.marketdata.PriceHistory;

public class PriceDataJdbiRepository implements PriceDataRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private static final String TABLE_PRICE_DATA = "PRICE_DATA";
    private static final String COLUMN_TICKER = "TICKER";
    private static final String COLUMN_DATE = "DATE";
    private static final String COLUMN_CLOSE_PRICE = "CLOSE_PRICE";
    
    private final Jdbi jdbi;
    
    public PriceDataJdbiRepository(DataSource dataSource) {
        jdbi = Jdbi.create(dataSource);
    }

    public PriceHistory getPriceHistory(Assets assets) {
        String sql = String.format("SELECT * FROM %s WHERE %s IN (<tickers>)", TABLE_PRICE_DATA, COLUMN_TICKER);
        
        List<String> tickers = assets.stream().map(Asset::ticker).collect(toList());
        
        List<PriceData> priceDataList = jdbi.withHandle(handle -> 
            handle.createQuery(sql)
                .bindList("tickers", tickers)
                .map((rs, ctx) -> mapToPriceData(rs)).list());
        
        Map<Asset, Map<LocalDate, Double>> priceMap = priceDataList.stream()
                .collect(groupingBy(priceData -> assets.findByTicker(priceData.ticker()), 
                        toMap(PriceData::date, PriceData::price)));
        
        return new PriceHistory(priceMap);
    }
    
    private static PriceData mapToPriceData(ResultSet rs) throws SQLException {
        return new PriceData(
                rs.getString(COLUMN_TICKER),
                rs.getDate(COLUMN_DATE).toLocalDate(),
                rs.getDouble(COLUMN_CLOSE_PRICE));
    }

    @Override
    public boolean savePriceData(PriceData priceData) {
        Map<String, Object> values = new HashMap<>();
        values.put(COLUMN_TICKER, priceData.ticker());
        values.put(COLUMN_DATE, priceData.date());
        values.put(COLUMN_CLOSE_PRICE, priceData.price());
        
        try {
            jdbi.withHandle(handle -> JdbiUtil.createInsert(handle, TABLE_PRICE_DATA, values).execute());
            //logger.info("Price data saved: {}", priceData);
            return true;
        } catch(Exception ex) {
            if(ex.getCause() instanceof SQLIntegrityConstraintViolationException) {
                logger.info("Already have price data for '{}' for {}", priceData.ticker(), priceData.date());
                return false;
            } else {
                throw new RuntimeException(ex);
            }
        }
    }

}
