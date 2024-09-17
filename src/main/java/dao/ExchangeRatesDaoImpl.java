package dao;

import dto.CurrencyDto;
import dto.ExchangeRatesDto;
import entity.Currency;
import exception.UniqueException;
import service.CurrencyService;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesDaoImpl implements ExchangeRatesDao{

    private final String FIND_ALL_SQL = "select  * from exchangerates";
    private final String FIND_BU_ID_CODES_SQL = """
            SELECT * FROM exchangerates WHERE (basecurrencyid = ? AND targetcurrencyid = ?);
            """;

    private final String SAVE_SQL = "insert into exchangerates(basecurrencyid, targetcurrencyid, rate) VALUES (?, ? , ?)";
    public final static ExchangeRatesDaoImpl INSTANCE = new ExchangeRatesDaoImpl();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    public static ExchangeRatesDaoImpl getInstance() {
        return INSTANCE;
    }

    private ExchangeRatesDaoImpl(){

    }


    @Override
    public List<ExchangeRatesDto> findAll() {
        try(Connection con = ConnectionManager.get();
            var statement = con.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = statement.executeQuery();
            ArrayList<ExchangeRatesDto> result = new ArrayList<>();
            while (resultSet.next()){
                CurrencyDto bCur = currencyService.findById(resultSet.getLong("basecurrencyid"));
                CurrencyDto tCur = currencyService.findById(resultSet.getLong("targetcurrencyid"));
                result.add(ExchangeRatesDto.builder().baseCurrency(bCur).targetCurrency(tCur).rate(resultSet.getDouble("rate")).build());
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExchangeRatesDto findByIdCodes(long firstId, long secondId) {
        try(Connection connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_BU_ID_CODES_SQL)) {
            statement.setLong(1, firstId);
            statement.setLong(2, secondId);
            var result = statement.executeQuery();
            if (result.next()){
                CurrencyDto bCur = currencyService.findById(result.getLong("basecurrencyid"));
                CurrencyDto tCur = currencyService.findById(result.getLong("targetcurrencyid"));
                return ExchangeRatesDto.builder().baseCurrency(bCur).targetCurrency(tCur).rate(result.getDouble("rate")).build();
            }
            // todo throw
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long save(long baseCur, long targetCur, Double rate) {
        try(Connection connection = ConnectionManager.get();
            var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS))  {
            statement.setLong(1, baseCur);
            statement.setLong(2, targetCur);
            statement.setDouble(3, rate);
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if(keys.next()) return keys.getLong("id");
        } catch (SQLException e) {
            throw new UniqueException(e);
        }
        return null;
    }


}
