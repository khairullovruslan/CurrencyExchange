package entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeRates {
    private Long id;
    private int baseCurrencyId;
    private int targetCurrencyId;
    private double rate;
}
