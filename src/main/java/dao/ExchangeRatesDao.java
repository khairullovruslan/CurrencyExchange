package dao;

import dto.ExchangeRatesDto;
import entity.Currency;
import entity.ExchangeRates;

import java.util.List;
import java.util.Map;

public interface ExchangeRatesDao {
    List<ExchangeRatesDto> findAll();

    Map<ExchangeRatesDto, Long> findByIdCodes(long firstId, long secondId);

    Long save(long baseCur, long targetCur, Double rate);

    ExchangeRatesDto changeRate( Map<ExchangeRatesDto, Long> data, Double rate);

}
