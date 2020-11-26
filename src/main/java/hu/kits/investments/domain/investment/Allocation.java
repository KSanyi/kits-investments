package hu.kits.investments.domain.investment;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import hu.kits.investments.domain.asset.Asset;

public class Allocation {

    private final Map<Asset, Integer> assetWeights;

    public Allocation(Map<Asset, Integer> assetWeights) {
        Collection<Integer> weights = assetWeights.values();
        if(weights.stream().anyMatch(weight -> weight < 0)) throw new IllegalArgumentException("Can't have negative weights");
        if(weights.stream().mapToInt(Integer::intValue).sum() > 100) throw new IllegalArgumentException("Sum of asset weight must be <= 100");
        this.assetWeights = Map.copyOf(assetWeights);
    }
    
    public int weightOf(Asset asset) {
        return assetWeights.getOrDefault(asset, 0);
    }
    
    public Set<Asset> assets() {
        return assetWeights.keySet();
    }
    
    public int cashWeight() {
        return assetWeights.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(assetWeights);
    }
    
    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
}
