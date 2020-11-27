package hu.kits.investments.domain.math;

import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.kits.investments.common.CollectionsUtil;
import hu.kits.investments.domain.asset.Asset;
import hu.kits.investments.domain.marketdata.PriceData;
import hu.kits.investments.domain.marketdata.PriceHistory;

public class YieldCorrelationMatrix {

    private final Map<Asset, Map<Asset, Double>> matrix;
    
    private YieldCorrelationMatrix(Map<Asset, Map<Asset, Double>> matrix) {
        this.matrix = matrix;
    }
    
    public Set<Asset> assets() {
        return matrix.keySet();
    }

    public double value(Asset asset1, Asset asset2) {
        return matrix.getOrDefault(asset1, emptyMap()).get(asset2);
    }
    
    public static YieldCorrelationMatrix create(PriceHistory priceHistory) {
        
        checkPriceHistory(priceHistory);
        
        Map<Asset, Map<Asset, Double>> matrix = new HashMap<>();
        
        List<Asset> assets = priceHistory.assets();
        
        List<Asset> assetList = new ArrayList<>(assets);
        
        for(int i=0;i<assetList.size();i++) {
            Asset asset1 = assetList.get(i);
            for(int j=i;j<assetList.size();j++) {
                Asset asset2 = assetList.get(j);
                
                double correlation = i == j ? 1.0 : calculateCorrelation(asset1, asset2, priceHistory);
                matrix.merge(asset1, Map.of(asset2, correlation), CollectionsUtil::merge);
                matrix.merge(asset2, Map.of(asset1, correlation), CollectionsUtil::merge);
            } 
        }
        
        return new YieldCorrelationMatrix(matrix);
    }
    
    private static void checkPriceHistory(PriceHistory priceHistory) {
        List<Asset> assets = priceHistory.assets();
        Asset asset0 = assets.iterator().next();
        
        List<LocalDate> priceHistoryDates = priceHistory.getPriceDatas(asset0).stream().map(PriceData::date).collect(toList());
        if(assets.stream().anyMatch(asset -> !priceHistoryDates.equals(priceHistory.getPriceDatas(asset).stream().map(PriceData::date).collect(toList())))) {
            System.out.println(priceHistory.printStats());
            throw new IllegalArgumentException("Price history dates must be the same for all assets");
        }
    }

    private static double calculateCorrelation(Asset asset1, Asset asset2, PriceHistory priceHistory) {
        double[] prices1 = priceHistory.getPriceDatas(asset1).stream().mapToDouble(p -> p.price().doubleValue()).toArray();
        double[] yields1 = calculateYields(prices1);
        double[] prices2 = priceHistory.getPriceDatas(asset2).stream().mapToDouble(p -> p.price().doubleValue()).toArray();
        double[] yields2 = calculateYields(prices2);
        return KitsStat.correlation(yields1, yields2);
    }
    
    private static double[] calculateYields(double[] prices) {
        double[] yields = new double[prices.length-1];
        for(int i=0;i<prices.length-1;i++) {
            yields[i] = prices[i+1] / prices[i] - 1;
        }
        return yields;
    }
    
    @Override
    public String toString() {
        
        NumberFormat formatter = new DecimalFormat("#0.000");
        
        List<Asset> assets = matrix.keySet().stream().sorted(comparing(Asset::ticker)).collect(toList());
        
        StringBuilder sb = new StringBuilder(assets.stream().map(Asset::ticker).collect(joining("\t", " \t", "\n")));
        
        for(Asset asset1 : assets) {
            sb.append(asset1.ticker()).append("\t");
            for(Asset asset2 : assets) {
                double correlation = matrix.get(asset1).get(asset2);
                sb.append(formatter.format(correlation)).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
}
