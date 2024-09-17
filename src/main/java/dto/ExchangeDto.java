package dto;

import entity.Currency;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ExchangeDto(CurrencyDto baseCurrency, CurrencyDto targetCurrency, Double rate, Double amount, BigDecimal convertedAmount){
}
