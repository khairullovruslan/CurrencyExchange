package dao;

import dto.CurrencyDto;
import entity.Currency;

import java.util.List;

public interface CurrencyDao {
    List<CurrencyDto> findAll();

    CurrencyDto findByCode(String currencyCode);
    Currency save(Currency currency);
}
