package hu.kits.investments.infrastructure.marketdata.bamosz;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.cj.jdbc.MysqlDataSource;

import hu.kits.investments.domain.asset.Asset;
import hu.kits.investments.domain.asset.Asset.AssetClass;
import hu.kits.investments.domain.asset.AssetRepository;
import hu.kits.investments.domain.asset.Assets;
import hu.kits.investments.domain.marketdata.PriceData;
import hu.kits.investments.domain.marketdata.PriceDataRepository;
import hu.kits.investments.infrastructure.database.AssetJdbiRepository;
import hu.kits.investments.infrastructure.database.PriceDataJdbiRepository;

public class BamoszImporter {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(new Locale("HU"));
    
    private static final DataSource dataSource = createDataSource();
    private static final PriceDataRepository priceDataRepository = new PriceDataJdbiRepository(dataSource);
    private static final AssetRepository assetRepository = new AssetJdbiRepository(dataSource);
    private static final Assets assets = assetRepository.loadAssets();
    
    private static final String BASE_URL = "https://www.bamosz.hu/bamosz-public-alapoldal-portlet/kuka.download?separator=pontosvesszo&isin=";
    
    public static void main(String[] args) throws Exception {

        Map<Asset, Integer> assetSavedMap = assets.forAssetClass(AssetClass.MUTUAL_FUND).stream().collect(toMap(
                asset -> asset,
                asset -> importBamoszData(asset)));
        
        assetSavedMap.forEach((asset, entriesSaved) -> logger.info("{}: {}", asset.name(), entriesSaved + " entries saved"));
    }
    
    private static int importBamoszData(Asset asset) {
        
        int numberOfEntriesSaved = 0;
        
        try {
            URL rowdata = new URL(BASE_URL + asset.isin());
            URLConnection data = rowdata.openConnection();
            try(Scanner input = new Scanner(data.getInputStream())) {
                for(int i=0;i<18;i++) {
                    input.hasNextLine();
                    input.nextLine();
                }

                while (input.hasNextLine()) {
                    String line = input.nextLine();
                    Optional<PriceData> priceData = parsePriceData(asset, line);
                    if(priceData.isPresent()) {
                        boolean success = priceDataRepository.savePriceData(priceData.get());
                        if(success) {
                            logger.info("Price data saved: {}", priceData.get());
                            numberOfEntriesSaved++;
                        } else {
                            return numberOfEntriesSaved;
                        }
                    }
                }
                return numberOfEntriesSaved;
            }
        } catch(IOException ex) {
            logger.error("Error import data for : {}", asset, ex);
            return numberOfEntriesSaved;
        }
    }
    
    private static Optional<PriceData> parsePriceData(Asset asset, String line) {
        
        String[] parts = line.replaceAll("\"", "").split(";");
        LocalDate date = LocalDate.parse(parts[0], DATE_FORMAT);
        double price;
        try {
            price = NUMBER_FORMAT.parse(parts[1]).doubleValue();
            return Optional.of(new PriceData(asset.ticker(), date, price));
        } catch (Exception ex) {
            logger.error("Error parsing: {}", line);
            return Optional.empty();
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
