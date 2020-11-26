package hu.kits.investments.domain.marketdata;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.asset.Asset;
import hu.kits.investments.domain.asset.Assets;

public class PriceDataService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final PriceDataSource priceDataSource;
    
    private final PriceDataRepository priceDataRepository;

    public PriceDataService(PriceDataSource priceDataSource, PriceDataRepository priceDataRepository) {
        this.priceDataSource = priceDataSource;
        this.priceDataRepository = priceDataRepository;
    }
    
    public PriceHistory getPriceHistory(Assets assets) {
        logger.debug("Loading price history");
        PriceHistory priceHistory = priceDataRepository.getPriceHistory(assets);
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
