package hu.kits.investments.domain.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {

    public static BigDecimal divideRound(double dividend, BigDecimal divisor) {
        return new BigDecimal(dividend).divide(divisor, RoundingMode.HALF_UP);
    }
    
    public static BigDecimal divideFloor(double dividend, BigDecimal divisor) {
        return new BigDecimal(dividend).divide(divisor, RoundingMode.FLOOR);
    }
    
    public static BigDecimal divideCeil(double dividend, BigDecimal divisor) {
        return new BigDecimal(dividend).divide(divisor, RoundingMode.CEILING);
    }
    
}
