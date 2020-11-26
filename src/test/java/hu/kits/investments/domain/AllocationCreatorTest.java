package hu.kits.investments.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import hu.kits.investments.domain.asset.Asset;
import hu.kits.investments.domain.asset.Asset.AssetClass;
import hu.kits.investments.domain.investment.Allocation;
import hu.kits.investments.domain.optimization.AllocationCreator;

public class AllocationCreatorTest {

    private final Asset asset1 = new Asset("AAPL", "", AssetClass.EQUITY, "");
    private final Asset asset2 = new Asset("BAX", "", AssetClass.EQUITY, "");
    private final Asset asset3 = new Asset("GOOG", "", AssetClass.EQUITY, "");
    
    @Test
    void twoAssets() {
        
        List<Allocation> allocations = AllocationCreator.createAllocations(List.of(asset1, asset2), 20);
        
        List<Allocation> expectedAllocations = List.of(
                new Allocation(Map.of(asset1,   0, asset2, 100)),
                new Allocation(Map.of(asset1,  20, asset2,  80)),
                new Allocation(Map.of(asset1,  40, asset2,  60)),
                new Allocation(Map.of(asset1,  60, asset2,  40)),
                new Allocation(Map.of(asset1,  80, asset2,  20)),
                new Allocation(Map.of(asset1, 100, asset2,   0))
                );
        
        Assertions.assertEquals(expectedAllocations, allocations);
    }
    
    @Test
    void threeAssets() {
        
        List<Allocation> allocations = AllocationCreator.createAllocations(List.of(asset1, asset2, asset3), 10);
        
        Assertions.assertEquals(allocations.size(), new HashSet<>(allocations).size());
        Assertions.assertTrue(allocations.stream().allMatch(a -> a.weightOf(asset1) + a.weightOf(asset2) + a.weightOf(asset3) == 100));
        Assertions.assertTrue(allocations.stream().allMatch(a -> a.weightOf(asset1) % 10 == 0 && a.weightOf(asset2) % 10 == 0 && a.weightOf(asset3) % 10 == 0));
    }
    
}
