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
    public CurrencyDto findById(long id) {return currencyDao.findById(id);}
    public Currency save(Currency currency){return currencyDao.save(currency);}
    public List<Currency> findByLikeCode(String c){return currencyDao.findByLikeCode(c);}

    public Currency findByCodeWithId(String secondCode) {return currencyDao.findByCodeWithId(secondCode);}
}
