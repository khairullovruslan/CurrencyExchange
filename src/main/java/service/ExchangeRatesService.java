package service;

import dao.ExchangeRatesDao;
import dao.ExchangeRatesDaoImpl;
import dto.CurrencyDto;
import dto.ExchangeDto;
import dto.ExchangeRatesDto;
import entity.Currency;
import exception.NotFoundException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private long[] splitCodes(String exCode){
        List<Currency> curList = currencyService.findByLikeCode(String.valueOf(exCode.charAt(0)));
        long[] id = new long[2];
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
                return new long[]{currency.getId(), currencyService.findByCodeWithId(secondCode).getId()};
            }
        }
        return null;
    }

    public ExchangeRatesDto findByIdCodes(String exCode) {
        long[] id = splitCodes(exCode);
        if (id == null || id.length != 2){
            throw new NotFoundException();
        }
        var res = exchangeRatesDao.findByIdCodes(id[0], id[1]);
        return res.keySet().stream().findFirst().get();
    }
    public ExchangeRatesDto saveNewExchange(String baseCurrencyCode, String targetCurrencyCode, Double rate){
        Currency baseCur = currencyService.findByCodeWithId(baseCurrencyCode);
        Currency targetCur = currencyService.findByCodeWithId(targetCurrencyCode);
        if (baseCur == null ||targetCur == null) throw new NotFoundException();

        exchangeRatesDao.save(baseCur.getId(), targetCur.getId(), rate);
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

    public ExchangeRatesDto changeRate(String exCode, Double rate) {
        long[] id = splitCodes(exCode);
        if (id == null || id.length != 2){
            throw new NotFoundException();
        }
        return exchangeRatesDao.changeRate(exchangeRatesDao.findByIdCodes(id[0], id[1]), rate);

    }


    public ExchangeDto exchange(String baseCurCode, String targetCurCode, Double amount) {
        Currency baseCur = currencyService.findByCodeWithId(baseCurCode);
        Currency targetCur = currencyService.findByCodeWithId(targetCurCode);
        if(baseCur == null || targetCur == null) throw new NotFoundException();
        var map = exchangeRatesDao.findByIdCodes(baseCur.getId(), targetCur.getId());
        if (map != null){
            return convertMapToExchangeDto(map, amount, false);
        }
        var reverseMap = exchangeRatesDao.findByIdCodes(targetCur.getId(), baseCur.getId());
        if (reverseMap != null){
            return convertMapToExchangeDto(reverseMap, amount, true);
        }
        Currency usdCur = currencyService.findByCodeWithId("USD");
        if (usdCur != null){
            var usdExToTarget = exchange("USD", targetCurCode, amount);
            var usdExToBase = exchange("USD", baseCurCode, amount);
            Double rate = usdExToBase.rate() / usdExToTarget.rate();
            return ExchangeDto
                    .builder()
                    .convertedAmount(BigDecimal.valueOf(rate * amount))
                    .rate(rate)
                    .baseCurrency(usdExToBase.targetCurrency())
                    .targetCurrency(usdExToBase.baseCurrency())
                    .build();
        }
        throw new NotFoundException();


    }

    private ExchangeDto convertMapToExchangeDto(Map<ExchangeRatesDto, Long> map, Double amount, boolean reverse){
        ExchangeRatesDto exchangeDto = map.keySet().stream().findFirst().get();
        return ExchangeDto
                .builder()
                .baseCurrency(!reverse ? exchangeDto.baseCurrency() : exchangeDto.targetCurrency())
                .targetCurrency(!reverse ? exchangeDto.targetCurrency() : exchangeDto.baseCurrency())
                .amount(amount)
                .rate(!reverse ? exchangeDto.rate() : 1 / exchangeDto.rate())
                .convertedAmount(!reverse ? BigDecimal.valueOf(exchangeDto.rate() * amount) : BigDecimal.valueOf((1 / exchangeDto.rate()) * amount) )
                .build();
    }
}
