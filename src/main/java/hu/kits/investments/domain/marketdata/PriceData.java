package hu.kits.investments.domain.marketdata;

import java.time.LocalDate;

import hu.kits.investments.domain.Asset;

public record PriceData(Asset asset, LocalDate date, double price) {

}
