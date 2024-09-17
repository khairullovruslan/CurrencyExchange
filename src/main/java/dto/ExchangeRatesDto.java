package dto;

import lombok.Builder;

@Builder
public record ExchangeRatesDto(CurrencyDto baseCurrency, CurrencyDto targetCurrency, double rate) {
}
