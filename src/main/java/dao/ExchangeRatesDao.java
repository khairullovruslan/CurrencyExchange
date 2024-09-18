package dao;

import entity.ExchangeRates;

import java.util.Optional;

public interface ExchangeRatesDao extends CrudDao<ExchangeRates> {


    Optional<ExchangeRates> save(String baseCode, String targetCode, Double rate);

    Optional<ExchangeRates> changeRate(String code, String code1, Double rate);

    Optional<ExchangeRates> findByCodes(String substring, String substring1);

    Optional<ExchangeRates> findByCodesAndReverse(String baseCurCode, String targetCurCode);
}
