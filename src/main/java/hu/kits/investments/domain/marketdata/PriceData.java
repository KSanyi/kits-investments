package hu.kits.investments.domain.marketdata;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PriceData(String ticker, LocalDate date, BigDecimal price) {

}
