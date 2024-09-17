package service;

import dao.ExchangeRatesDao;
import dao.ExchangeRatesDaoImpl;
import dto.CurrencyDto;
import dto.ExchangeRatesDto;
import entity.Currency;
import entity.ExchangeRates;
import exception.NotFoundException;

import java.util.List;

public class ExchangeRatesService {
    private final static ExchangeRatesService INSTANCE = new ExchangeRatesService();
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDaoImpl.getInstance();
    public static ExchangeRatesService getInstance() {
        return INSTANCE;
    }
    private final CurrencyService currencyService = CurrencyService.getInstance();

    private ExchangeRatesService(){}

    public List<ExchangeRatesDto> findAll(){
        return exchangeRatesDao.findAll();
    }

    public ExchangeRatesDto findByIdCodes(String exCode) {
        List<Currency> curList = currencyService.findByLikeCode(String.valueOf(exCode.charAt(0)));
        for (Currency currency: curList){
            if (exCode.contains(currency.getCode())){
                int ind = exCode.indexOf(currency.getCode());
                String secondCode;
                if (ind == 0){
                    secondCode = exCode.substring(currency.getCode().length());
                }
                else {
                    secondCode = exCode.substring(0, currency.getCode().length());
                }
                return exchangeRatesDao.findByIdCodes(currency.getId(), currencyService.findByCodeWithId(secondCode).getId());
            }
        }
        return null;
    }
    public ExchangeRatesDto saveNewExchange(String baseCurrencyCode, String targetCurrencyCode, Double rate){
        Currency baseCur = currencyService.findByCodeWithId(baseCurrencyCode);
        Currency targetCur = currencyService.findByCodeWithId(targetCurrencyCode);
        if (baseCur == null ||targetCur == null) throw new NotFoundException();

        Long id = exchangeRatesDao.save(baseCur.getId(), targetCur.getId(), rate);
        return ExchangeRatesDto
                .builder()
                .baseCurrency(
                        CurrencyDto
                                .builder()
                                .sign(baseCur.getSign())
                                .fullName(baseCur.getFullName())
                                .code(baseCur.getCode())
                                .build())
                .targetCurrency(
                        CurrencyDto
                                .builder()
                                .sign(targetCur.getSign())
                                .fullName(targetCur.getFullName())
                                .code(targetCur.getCode())
                                .build())
                .rate(rate)
                .build();

    }
}
