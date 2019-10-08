package hu.kits.investments.domain.marketdata;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Set;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.Asset;

public class PriceHistory {

    private final Map<Asset, Map<LocalDate, Double>> priceMap;

    public PriceHistory(Map<Asset, Map<LocalDate, Double>> priceMap) {
        this.priceMap = Map.copyOf(priceMap);
    }
    
    public Set<Asset> assets() {
        return priceMap.keySet();
    }
    
    public Optional<Double> findPrice(Asset asset, LocalDate date) {
        
        Double price = priceMap.getOrDefault(asset, emptyMap()).get(date);
        int counter = 1;
        while(price == null && counter < 10) {
            price = priceMap.getOrDefault(asset, emptyMap()).get(date.minusDays(counter));
            counter++;
        }
        return Optional.ofNullable(price);
    }
    
    public double getPrice(Asset asset, LocalDate date) {
        
        return findPrice(asset, date).get();
    }
    
    public List<PriceData> getPriceDatas(Asset asset) {
        return priceMap.getOrDefault(asset, emptyMap()).entrySet().stream()
            .map(e -> new PriceData(asset, e.getKey(), e.getValue()))
            .collect(toList());
    }
    
    public AssetPrices assetPricesAt(LocalDate date) {
        
        Map<Asset, Double> assetPriceMap = priceMap.entrySet().stream()
                .collect(toMap(Entry::getKey, e -> findPrice(e.getKey(), date))).entrySet().stream()
                .filter(e -> e.getValue().isPresent())
                .collect(toMap(Entry::getKey, e -> e.getValue().get()));
        
        return new AssetPrices(assetPriceMap);
    }
    
    public PriceHistory in(DateRange dateRange) {
        Map<Asset, Map<LocalDate, Double>> result = priceMap.entrySet().stream()
                .collect(toMap(Entry::getKey, e -> in(e.getValue(), dateRange)));
        
        return new PriceHistory(result);
    }
    
    private static Map<LocalDate, Double> in(Map<LocalDate, Double> priceMap, DateRange dateRange) {
        return priceMap.entrySet().stream()
                .filter(e -> dateRange.contains(e.getKey()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
    
    public PriceHistory until(LocalDate lastDate) {
        return new PriceHistory(priceMap) {
            
            @Override
            public double getPrice(Asset asset, LocalDate date) {
                if(date.isAfter(lastDate)) {
                    throw new IllegalArgumentException("Illegal price query for date " + date);
                } else {
                    return super.getPrice(asset, lastDate);
                }
            }
            
            @Override
            public AssetPrices assetPricesAt(LocalDate date) {
                if(date.isAfter(lastDate)) {
                    throw new IllegalArgumentException("Illegal price query for date " + date);
                } else {
                    return super.assetPricesAt(lastDate);
                }
            }
        };
    }

}
