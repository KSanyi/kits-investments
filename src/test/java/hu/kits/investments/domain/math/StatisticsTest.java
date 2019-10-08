package hu.kits.investments.domain.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StatisticsTest {

    @Test
    void correlationSimple() {
        
        double[] x = {1, 3};
        double[] y = {3, 1};
        
        assertEquals(1, KitsStat.correlation(x, x), 0.001);
        assertEquals(-1, KitsStat.correlation(x, y), 0.001);
    }
    
    @Test
    void correlation() {
        
        double[] x = {1692, 1978, 1884, 2151, 2519};
        double[] y = {69, 100, 109, 112, 154};
        
        assertEquals(0.95, KitsStat.correlation(x, y), 0.01);
    }
    
}
