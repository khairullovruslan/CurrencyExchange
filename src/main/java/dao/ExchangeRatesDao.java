package dao;

import dto.ExchangeRatesDto;
import entity.Currency;

import java.util.List;

public interface ExchangeRatesDao {
    List<ExchangeRatesDto> findAll();

    ExchangeRatesDto findByIdCodes(long firstId, long secondId);

    Long save(long baseCur, long targetCur, Double rate);
}
