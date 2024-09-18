package dao;

import entity.ExchangeRates;

import java.util.Optional;

public interface ExchangeRatesDao extends CrudDao<ExchangeRates>{


    Long save(String baseCode, String targetCode, Double rate);

    boolean changeRate(String code, Double rate);

    Optional<ExchangeRates> findByCodes(String substring, String substring1);

    Optional<ExchangeRates> findByCodesAndReverse(String baseCurCode, String targetCurCode);
}
