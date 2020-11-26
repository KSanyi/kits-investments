package hu.kits.investments.domain.marketdata;

import java.time.LocalDate;

public record PriceData(String ticker, LocalDate date, double price) {

}
