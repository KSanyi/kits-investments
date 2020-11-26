package hu.kits.investments.domain.portfolio;

import java.time.LocalDate;

public record CashMovement(LocalDate date, int amount) {

}
