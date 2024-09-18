package service;

import dao.ExchangeRatesDao;
import dao.ExchangeRatesDaoImpl;
import dto.CurrencyDto;
import dto.ExchangeDto;
import dto.ExchangeRatesDto;
import entity.ExchangeRates;
import exception.DaoException;
import exception.NotFoundException;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesService {
    private final static ExchangeRatesService INSTANCE = new ExchangeRatesService();
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDaoImpl.getInstance();

    public static ExchangeRatesService getInstance() {
        return INSTANCE;
    }

    private final ModelMapper mapper = new ModelMapper();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    private ExchangeRatesService() {
    }

    public List<ExchangeRatesDto> findAll() {
        return exchangeRatesDao.findAll().stream().map(s -> mapper.map(s, ExchangeRatesDto.class)).toList();
    }


    public ExchangeRatesDto findByCodes(String exCode) {
        var res = exchangeRatesDao.findByCodes(exCode.substring(0, 3), exCode.substring(3, 6));
        if (res.isPresent()) return mapper.map(res.get(), ExchangeRatesDto.class);
        throw new NotFoundException();
    }

    public ExchangeRatesDto saveNewExchange(String baseCurrencyCode, String targetCurrencyCode, Double rate) {
        Optional<ExchangeRates> exchangeRates = exchangeRatesDao.save(baseCurrencyCode, targetCurrencyCode, rate);
        if (exchangeRates.isPresent()) return mapper.map(exchangeRates.get(), ExchangeRatesDto.class);
        throw new DaoException();
    }

    public ExchangeRatesDto changeRate(String exCode, Double rate) {

        Optional<ExchangeRates> exchangeRates = exchangeRatesDao.changeRate(exCode.substring(0, 3), exCode.substring(3, 6), rate);
        if (exchangeRates.isPresent()) return mapper.map(exchangeRates.get(), ExchangeRatesDto.class);
        throw new NotFoundException();

    }


    public ExchangeDto exchange(String baseCurCode, String targetCurCode, Double amount) {
        Optional<ExchangeRates> res = exchangeRatesDao.findByCodesAndReverse(baseCurCode, targetCurCode);
        if (res.isPresent()) {
            ExchangeRates exchangeRates = res.get();
            return convertExchangeRatesToExchangeDto(exchangeRates, amount, !exchangeRates.getBaseCurrency().getCode().equals(baseCurCode));
        }
        throw new NotFoundException();
    }

    private ExchangeDto convertExchangeRatesToExchangeDto(ExchangeRates exchangeRates, Double amount, boolean reverse) {
        return ExchangeDto
                .builder()
                .baseCurrency(!reverse ? mapper.map(exchangeRates.getBaseCurrency(), CurrencyDto.class)
                        : mapper.map(exchangeRates.getTargetCurrency(), CurrencyDto.class))
                .targetCurrency(!reverse ? mapper.map(exchangeRates.getTargetCurrency(), CurrencyDto.class)
                        : mapper.map(exchangeRates.getBaseCurrency(), CurrencyDto.class))
                .amount(amount)
                .rate(!reverse ? exchangeRates.getRate() : 1 / exchangeRates.getRate())
                .convertedAmount(!reverse ? BigDecimal.valueOf(exchangeRates.getRate() * amount) : BigDecimal.valueOf((1 / exchangeRates.getRate()) * amount))
                .build();
    }
}
