package hu.kits.investments.domain.optimization;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.kits.investments.domain.Asset;
import hu.kits.investments.domain.investment.Allocation;

public class AllocationCreator {

    public static List<Allocation> createAllocations(List<Asset> assets, int stepPercent) {
        
        if(stepPercent > 100 || stepPercent < 0) throw new IllegalArgumentException("Step must be between 0 and 100");
        
        List<Map<Asset, Integer>> allocationMaps = createAllocations(assets, stepPercent, 100);
        return allocationMaps.stream().map(Allocation::new).collect(toList());
    }
    
    private static List<Map<Asset, Integer>> createAllocations(List<Asset> assets, int step, int sum) {
        
        Asset asset = assets.get(0);
        
         if(assets.size() == 1) {
             return List.of(Map.of(asset, sum));
         }
         
         int weight = 0;
         List<Map<Asset, Integer>> allAllocations = new ArrayList<>();
         while(weight <= sum) {
             List<Map<Asset, Integer>> allocations = createAllocations(removeAsset(assets, asset), step, sum - weight);
             allocations = addEntryToAllocations(allocations, asset, weight);
             allAllocations.addAll(allocations);
             weight += step;
         }
         
         return allAllocations;
    }
    
    private static List<Asset> removeAsset(List<Asset> assets, Asset assetToRemove) {
        return assets.stream().filter(asset -> !asset.equals(assetToRemove)).collect(toList());
    }
    
    private static List<Map<Asset, Integer>> addEntryToAllocations(List<Map<Asset, Integer>> allocations, Asset asset, int weight) {
        return allocations.stream().map(m -> add(m, asset, weight)).collect(toList());
    }
    
    private static Map<Asset, Integer> add(Map<Asset, Integer> map, Asset asset, int weight) {
        Map<Asset, Integer> result = new HashMap<>(map);
        result.put(asset, weight);
        return Map.copyOf(result);
    }
}
