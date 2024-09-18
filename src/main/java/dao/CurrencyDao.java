package dao;

import entity.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyDao  extends CrudDao<Currency>{
    Optional<Currency> findByCode(String code);
    List<Currency> findByLikeCode(String code);
}
