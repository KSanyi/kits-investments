package hu.kits.investments.common;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Formatters {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy. MMMM dd. HH:mm");
    private static final DecimalFormat DECIMAL_FORMAT;
    private static final DecimalFormat PERCENT_FORMAT;
    
    public static final Locale HU_LOCALE = new Locale("HU");
    
    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        decimalFormatSymbols.setGroupingSeparator(' ');
        DECIMAL_FORMAT = new DecimalFormat("###,###", decimalFormatSymbols);
        PERCENT_FORMAT = new DecimalFormat("0.00%", decimalFormatSymbols);
    }
    
    public static String formatDate(LocalDate date) {
        return DATE_FORMAT.format(date);
    }
    
    public static String formatDateTime(LocalDateTime date) {
        return  DATE_TIME_FORMAT.format(date);
    }
    
    public static String formatPercent(double value) {
        return PERCENT_FORMAT.format(value);
    }
    
    public static String formatDecimal(int value) {
        return DECIMAL_FORMAT.format(value);
    }
    
}
