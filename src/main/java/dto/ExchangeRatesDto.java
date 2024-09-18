package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRatesDto {
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    private double rate;
}
