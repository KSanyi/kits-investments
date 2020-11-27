package hu.kits.investments.domain;

import static hu.kits.investments.domain.TestAssets.AAPL;
import static hu.kits.investments.domain.TestAssets.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import hu.kits.investments.domain.investment.Allocation;
import hu.kits.investments.domain.optimization.AllocationCreator;

public class AllocationCreatorTest {

    @Test
    void twoAssets() {
        
        List<Allocation> allocations = AllocationCreator.createAllocations(List.of(AAPL, GOOG), 20);
        
        List<Allocation> expectedAllocations = List.of(
                new Allocation(Map.of(AAPL,   0, GOOG, 100)),
                new Allocation(Map.of(AAPL,  20, GOOG,  80)),
                new Allocation(Map.of(AAPL,  40, GOOG,  60)),
                new Allocation(Map.of(AAPL,  60, GOOG,  40)),
                new Allocation(Map.of(AAPL,  80, GOOG,  20)),
                new Allocation(Map.of(AAPL, 100, GOOG,   0))
                );
        
        Assertions.assertEquals(expectedAllocations, allocations);
    }
    
    @Test
    void threeAssets() {
        
        List<Allocation> allocations = AllocationCreator.createAllocations(List.of(AAPL, GOOG, AMZN), 10);
        
        Assertions.assertEquals(allocations.size(), new HashSet<>(allocations).size());
        Assertions.assertTrue(allocations.stream().allMatch(a -> a.weightOf(AAPL) + a.weightOf(GOOG) + a.weightOf(AMZN) == 100));
        Assertions.assertTrue(allocations.stream().allMatch(a -> a.weightOf(AAPL) % 10 == 0 && a.weightOf(GOOG) % 10 == 0 && a.weightOf(AMZN) % 10 == 0));
    }
    
}
