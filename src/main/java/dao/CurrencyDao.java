package dao;

import dto.CurrencyDto;
import entity.Currency;

import java.util.List;

public interface CurrencyDao  extends CrudDao<Currency>{
    Currency findByCode(String code);
    List<Currency> findByLikeCode(String code);
}
