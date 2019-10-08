package hu.kits.investments.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Formatters {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy. MMMM dd. HH:mm");
    
    public static final Locale HU_LOCALE = new Locale("HU");
    
    public static String formatDate(LocalDate date) {
        return DATE_FORMAT.format(date);
    }
    
    public static String formatDateTime(LocalDateTime date) {
        return  DATE_TIME_FORMAT.format(date);
    }
    
}
