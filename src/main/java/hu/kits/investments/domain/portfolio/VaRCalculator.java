package hu.kits.investments.domain.portfolio;

import java.util.List;

import hu.kits.investments.domain.math.KitsStat;
import hu.kits.investments.domain.portfolio.PortfolioStats.VaRAndES;

public class VaRCalculator {

    // TODO: decaying weights, horizon
    
    public static VaRAndES calculateVaRAndES(List<Double> dailyYields, double confidenceLevel) {
        List<Double> sortedYields = dailyYields.stream().sorted().toList();
        
        int n = sortedYields.size();
        int cutoffIndex = (int)Math.round(n * (1 - confidenceLevel / 100));
        
        double var = sortedYields.get(cutoffIndex);
        
        double es = KitsStat.average(sortedYields.subList(0, cutoffIndex));
        
        return new VaRAndES(var, es);
    }
    
}
