package dao;

import entity.ExchangeRates;
import java.util.Map;

public interface ExchangeRatesDao extends CrudDao<ExchangeRates>{

    Map<ExchangeRates, Long> findByIdCodes(long firstId, long secondId);

}
