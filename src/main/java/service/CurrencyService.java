package service;

import dao.CurrencyDao;
import dao.CurrencyDaoImp;
import dto.CurrencyDto;
import entity.Currency;
import exception.NotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.Optional;

public final class CurrencyService {
    private final static ModelMapper mapper;
    private final static CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDaoImp.getInstance();

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    static {
        mapper = new ModelMapper();

        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);


    }

    private CurrencyService(){

    }

    public List<CurrencyDto> findAll(){
        return currencyDao.findAll().stream().map(s -> mapper.map(s, CurrencyDto.class)).toList();
    }

    public CurrencyDto findByCode(String currencyCode) {
        Optional<Currency> currency = currencyDao.findByCode(currencyCode);
        if (currency.isEmpty()) throw new NotFoundException();
        return mapper.map(currency.get(), CurrencyDto.class);

    }
    public CurrencyDto findById(long id) {
        return mapper.map(currencyDao.findById(id), CurrencyDto.class);
    }

    //todo check if id field == null
    public Currency save(CurrencyDto currency){
        return currencyDao.save(mapper.map(currency, Currency.class));
    }

    public List<CurrencyDto> findByLikeCode(String c){
        return currencyDao.findByLikeCode(c).stream().map(s -> mapper.map(s, CurrencyDto.class)).toList();
    }

}
