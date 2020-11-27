package hu.kits.investments.domain.marketdata;

import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import hu.kits.investments.common.DateRange;
import hu.kits.investments.domain.asset.Asset;

public class PriceHistory {

    public final Map<Asset, Map<LocalDate, BigDecimal>> priceMap;

    public PriceHistory(Map<Asset, Map<LocalDate, BigDecimal>> priceMap) {
        //validate(priceMap);
        this.priceMap = Map.copyOf(priceMap);
    }
    
    private static void validate(Map<Asset, Map<LocalDate, BigDecimal>> priceMap) {
        Set<LocalDate> dates = priceMap.values().stream().map(Map::keySet).max(Comparator.comparing(Set::size)).get();
        
        for(Asset asset : priceMap.keySet()) {
            if(!priceMap.get(asset).keySet().equals(dates)) {
                throw new IllegalArgumentException("There is not enough price data for " + asset.ticker());
            }
        }
    }

    public List<Asset> assets() {
        return priceMap.keySet().stream().sorted(comparing(Asset::ticker)).collect(toList());
    }
    
    public List<LocalDate> dates() {
        return priceMap.values().stream().flatMap(map -> map.keySet().stream()).distinct().sorted().collect(toList());
    }
    
    public Optional<BigDecimal> findPrice(Asset asset, LocalDate date) {
        
        Map<LocalDate, BigDecimal> priceMapForAsset = priceMap.getOrDefault(asset, Map.of());
        BigDecimal price = priceMapForAsset.get(date);
        int counter = 1;
        while(price == null && counter < 10) {
            price = priceMapForAsset.get(date.minusDays(counter));
            counter++;
        }
        return Optional.ofNullable(price);
    }
    
    public BigDecimal getPrice(Asset asset, LocalDate date) {
        
        return findPrice(asset, date).get();
    }
    
    public List<PriceData> getPriceDatas(Asset asset) {
        return priceMap.getOrDefault(asset, emptyMap()).entrySet().stream()
            .map(e -> new PriceData(asset.ticker(), e.getKey(), e.getValue()))
            .collect(toList());
    }
    
    public AssetPrices assetPricesAt(LocalDate date) {
        
        Map<Asset, BigDecimal> assetPriceMap = priceMap.entrySet().stream()
                .collect(toMap(Entry::getKey, e -> findPrice(e.getKey(), date))).entrySet().stream()
                .filter(e -> e.getValue().isPresent())
                .collect(toMap(Entry::getKey, e -> e.getValue().get()));
        
        return new AssetPrices(assetPriceMap);
    }
    
    public PriceHistory in(DateRange dateRange) {
        Map<Asset, Map<LocalDate, BigDecimal>> result = priceMap.entrySet().stream()
                .collect(toMap(Entry::getKey, e -> in(e.getValue(), dateRange)));
        
        return new PriceHistory(result);
    }
    
    private static Map<LocalDate, BigDecimal> in(Map<LocalDate, BigDecimal> priceMap, DateRange dateRange) {
        return priceMap.entrySet().stream()
                .filter(e -> dateRange.contains(e.getKey()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
    
    public PriceHistory until(LocalDate lastDate) {
        return new PriceHistory(priceMap) {
            
            @Override
            public BigDecimal getPrice(Asset asset, LocalDate date) {
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
    
    @Override
    public String toString() {
        List<LocalDate> dates = dates(); 
        return assets().size() + " assets for " + dates.get(0) + " - " + dates.get(dates.size()-1); 
    }
    
    public String printStats() {
        return priceMap.entrySet().stream()
            .collect(toMap(e -> e.getKey(), e -> e.getValue().size()))
            .entrySet().stream()
            .map(e -> e.getKey() + ": " + e.getValue() + " entries")
            .collect(Collectors.joining("\n"));
    }

    public DateRange dateRange() {
        var dates = dates();
        return new DateRange(dates.get(0), dates.get(dates.size()-1));
    }

}
