package hu.kits.investments.domain.math;

import java.util.Collection;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class KitsStat {

    public static double average(Collection<? extends Number> values) {
        return average(values.stream().mapToDouble(Number::doubleValue).toArray());
    }
    
    public static double average(double[] values) {
        if (values.length == 0)
            throw new IllegalArgumentException("Cannot compute statistics without data");
        return DoubleStream.of(values).average().getAsDouble();
    }

    public static double stDev(Collection<? extends Number> values) {
        return stDev(values.stream().mapToDouble(Number::doubleValue).toArray());
    }
    
    public static double stDev(double[] values) {
        double average = average(values);
        return KitsMath.sqrt(DoubleStream.of(values).map(value -> KitsMath.square(value - average)).average().getAsDouble());
    }
    
    public static double covariance(double[] x, double[] y) {
        
        if(x.length != y.length) throw new IllegalArgumentException("Values must have the same size " + x.length + " != " + y.length);
        
        int size = x.length;
        double xAverage = average(x);
        double yAverage = average(y);
        
        return IntStream.range(0, size).mapToDouble(i -> (x[i] - xAverage) * (y[i] - yAverage)).average().getAsDouble();
    }
    
    public static double correlation(double[] x, double[] y) {
        return covariance(x, y) / (stDev(x) * stDev(y));
    }

}
