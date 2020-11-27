package hu.kits.investments.infrastructure.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;

import hu.kits.investments.domain.asset.Asset;
import hu.kits.investments.domain.asset.AssetRepository;
import hu.kits.investments.domain.asset.Assets;
import hu.kits.investments.domain.asset.Currency;

public class AssetJdbiRepository implements AssetRepository {
    
    private static final String TABLE_ASSET = "ASSET";
    private static final String COLUMN_TICKER = "TICKER";
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_ASSET_CLASS = "ASSET_CLASS";
    private static final String COLUMN_ISIN = "ISIN";
    private static final String COLUMN_CURRENCY = "CURRENCY";
    
    private final Jdbi jdbi;
    
    public AssetJdbiRepository(DataSource dataSource) {
        jdbi = Jdbi.create(dataSource);
    }

    public Assets loadAssets() {
        String sql = String.format("SELECT * FROM %s", TABLE_ASSET);
        
        List<Asset> assetList = jdbi.withHandle(handle -> 
            handle.createQuery(sql)
                .map((rs, ctx) -> mapToAsset(rs)).list());
        
        return new Assets(assetList);
    }
    
    private static Asset mapToAsset(ResultSet rs) throws SQLException {
        return new Asset(
                rs.getString(COLUMN_TICKER),
                rs.getString(COLUMN_NAME),
                Asset.AssetClass.valueOf(rs.getString(COLUMN_ASSET_CLASS)),
                rs.getString(COLUMN_ISIN),
                Currency.valueOf(rs.getString(COLUMN_CURRENCY)));
    }

}
