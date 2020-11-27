package hu.kits.investments.domain.marketdata;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.asset.Asset;
import hu.kits.investments.domain.asset.Assets;
import hu.kits.investments.domain.marketdata.fx.FXRateRepository;
import hu.kits.investments.domain.marketdata.fx.FXRates;

public class PriceDataService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final PriceDataSource priceDataSource;
    
    private final PriceDataRepository priceDataRepository;
    private final FXRateRepository fxRepository;

    public PriceDataService(PriceDataSource priceDataSource, PriceDataRepository priceDataRepository, FXRateRepository fxRepository) {
        this.priceDataSource = priceDataSource;
        this.priceDataRepository = priceDataRepository;
        this.fxRepository = fxRepository;
    }
    
    public PriceHistory getPriceHistory(Assets assets) {
        logger.debug("Loading price history");
        PriceHistory priceHistory = priceDataRepository.getPriceHistory(assets);
        FXRates fxRates = fxRepository.loadFXRates();
        
        var assetsMap = new HashMap<>(priceHistory.priceMap);
        var currencyAssetsMap = fxRates.ratesMap.entrySet().stream().collect(Collectors.toMap(e -> Asset.fromCurrency(e.getKey()), e -> e.getValue()));
        
        assetsMap.putAll(currencyAssetsMap);
        
        priceHistory = new PriceHistory(assetsMap);
        logger.info("Price history loaded: {}", priceHistory);
        return priceHistory;
    }
    
    public void fetchAndSavePriceData(Asset asset, LocalDate from) {
        
        List<PriceData> priceDatas = priceDataSource.getPriceData(asset, new DateRange(from, LocalDate.now()));
        
        logger.info("Received {} {} price datas from source", priceDatas.size(), asset);
        
        int savedPriceDataCount = 0;
        for(PriceData priceData : priceDatas) {
            boolean saved = priceDataRepository.savePriceData(priceData);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(saved) {
                savedPriceDataCount++;
            }
        }
        
        logger.info("{} {} price data saved into the repository", savedPriceDataCount, asset);
    }
    
}
