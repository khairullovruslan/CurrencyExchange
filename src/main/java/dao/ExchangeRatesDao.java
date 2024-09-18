package dao;

import dto.ExchangeRatesDto;
import entity.Currency;
import entity.ExchangeRates;

import java.util.List;
import java.util.Map;

public interface ExchangeRatesDao extends CrudDao<ExchangeRates>{

    Map<ExchangeRates, Long> findByIdCodes(long firstId, long secondId);

}
