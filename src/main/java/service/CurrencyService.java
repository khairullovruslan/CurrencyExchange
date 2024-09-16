package service;

import dao.CurrencyDao;
import dao.CurrencyDaoImp;
import dto.CurrencyDto;
import entity.Currency;

import java.util.List;

public final class CurrencyService {
    private final static CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDaoImp.getInstance();

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    private CurrencyService(){

    }

    public List<CurrencyDto> findAll(){
        return currencyDao.findAll();
    }

    public CurrencyDto findByCode(String currencyCode) {return currencyDao.findByCode(currencyCode);}
    public Currency save(Currency currency){return currencyDao.save(currency);}
}
