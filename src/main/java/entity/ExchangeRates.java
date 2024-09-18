package entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeRates {
    private Long id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private double rate;
}
