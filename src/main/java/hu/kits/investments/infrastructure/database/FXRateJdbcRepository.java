package hu.kits.investments.infrastructure.database;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;

import hu.kits.investments.domain.asset.Currency;
import hu.kits.investments.domain.marketdata.fx.FXRateRepository;
import hu.kits.investments.domain.marketdata.fx.FXRates;
import hu.kits.investments.domain.marketdata.fx.FXRates.FXRate;

public class FXRateJdbcRepository implements FXRateRepository {

    private static final String TABLE_FX_RATES = "FX_RATES";
    private static final String COLUMN_DATE = "DATE";
    private static final String COLUMN_CURRENCY = "CURRENCY";
    private static final String COLUMN_RATE = "RATE";
    
    private final Jdbi jdbi;
    
    public FXRateJdbcRepository(DataSource dataSource) {
        jdbi = Jdbi.create(dataSource);
    }
    
    @Override
    public FXRates loadFXRates() {
        
        String sql = String.format("SELECT * FROM %s", TABLE_FX_RATES);
        
        List<FXRate> fxRates = jdbi.withHandle(handle -> 
            handle.createQuery(sql).map((rs, ctx) -> mapToFXRate(rs)).list());
        
        return new FXRates(fxRates);
    }
    
    private static FXRate mapToFXRate(ResultSet rs) throws SQLException {
        return new FXRate(
                rs.getDate(COLUMN_DATE).toLocalDate(),
                Currency.valueOf(rs.getString(COLUMN_CURRENCY)),
                rs.getBigDecimal(COLUMN_RATE));
    }
        

    @Override
    public void saveFXRates(List<FXRate> fxRates) {
        
        fxRates.forEach(fxRate -> jdbi.withHandle(handle -> JdbiUtil.createInsert(handle, 
                TABLE_FX_RATES, Map.of(
                        COLUMN_DATE, fxRate.date(),
                        COLUMN_CURRENCY, fxRate.currency().name(),
                        COLUMN_RATE, fxRate.rateInHUF()))
            .execute()));
    }

    @Override
    public LocalDate getLatestPriceDate() {
        String sql = String.format("SELECT MAX(%s) as %s FROM %s", COLUMN_DATE, COLUMN_DATE, TABLE_FX_RATES);
        return jdbi.withHandle(handle -> handle.createQuery(sql)
                .map((rs, ctx) -> Optional.ofNullable(rs.getDate(COLUMN_DATE)).map(Date::toLocalDate).orElse(LocalDate.MIN))
                .findFirst()
                .orElse(LocalDate.MIN));
    }

}
