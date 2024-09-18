package dao;

import entity.Currency;
import entity.ExchangeRates;
import exception.UniqueException;
import service.CurrencyService;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ExchangeRatesDaoImpl implements ExchangeRatesDao{

    private final String FIND_ALL_SQL =

    """
    select * from exchangerates as exchange
    join currencies as currency on exchange.BaseCurrencyId = currency.id
    join currencies as currency2 on exchange.targetcurrencyid = currency2.id;
    """;

    private final String FIND_BY_ID_CODES_SQL =

    """
    select * from exchangerates as exchange
    join currencies as cur on exchange.BaseCurrencyId = cur.id
    join currencies as cur2 on exchange.targetcurrencyid = cur2.id where cur.code = ? and cur2.code = ?
    """;

    private final String FIND_BY_CODES_REVERSE_SQL =

    """
    select * from exchangerates as exchange
    join currencies as cur on exchange.BaseCurrencyId = cur.id
    join currencies as cur2 on exchange.targetcurrencyid = cur2.id
    where cur.code = ? and cur2.code = ? or cur.code = ? and cur2.code = ?  
    """;

    private final String SAVE_SQL = """
    
    insert into exchangerates(basecurrencyid, targetcurrencyid, rate)
    select
    (select id from currencies as cur1 where  cur1.code = ?),
    (select id from currencies as cur2 where  cur2.code = ?),
    ?;
    """;

    private final String UPDATE_SQl =

    """
    update exchangerates
    set rate = ? from exchangerates as exchange
    join currencies as cur on exchange.BaseCurrencyId = cur.id
    join currencies as cur2 on exchange.targetcurrencyid = cur2.id where cur.code = ? and cur2.code = ?
    """;


    public final static ExchangeRatesDaoImpl INSTANCE = new ExchangeRatesDaoImpl();

    public static ExchangeRatesDaoImpl getInstance() {
        return INSTANCE;
    }

    private ExchangeRatesDaoImpl(){

    }

    private List<ExchangeRates> convertResultSetToList(ResultSet resultSet) throws SQLException {
        ArrayList<ExchangeRates> result = new ArrayList<>();
        while (resultSet.next()){
            Currency baseCur = Currency
                    .builder()
                    .id(resultSet.getLong("currency.id"))
                    .code(resultSet.getString("currency.code"))
                    .fullName(resultSet.getString("currency.full_name"))
                    .sign(resultSet.getString("currency.sign"))
                    .build();
            Currency targetCur = Currency
                    .builder()
                    .id(resultSet.getLong("currency2.id"))
                    .code(resultSet.getString("currency2.code"))
                    .fullName(resultSet.getString("currency2.full_name"))
                    .sign(resultSet.getString("currency2.sign"))
                    .build();

            result.add(ExchangeRates
                    .builder()
                    .baseCurrency(baseCur).targetCurrency(targetCur).rate(resultSet.getDouble("rate")).build());
        }
        return result;
    }




    @Override
    public List<ExchangeRates> findAll() {
        try(Connection con = ConnectionManager.get();
            var statement = con.prepareStatement(FIND_ALL_SQL)) {
            return convertResultSetToList(statement.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Long save(String baseCode, String targetCode, Double rate) {
        try(Connection connection = ConnectionManager.get();
            var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS))  {
            statement.setString(1, baseCode);
            statement.setString(2, targetCode);
            statement.setDouble(3, rate);
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if(keys.next()) return keys.getLong("id");
        } catch (SQLException e) {
            throw new UniqueException(e);
        }
        return null;
    }

    @Override
    public boolean changeRate(String code, Double rate) {
        try (Connection connection = ConnectionManager.get();
            var statement = connection.prepareStatement(UPDATE_SQl)){
            String baseCode = code.substring(0 , 3);
            String targetCode = code.substring(3, 6);
            statement.setDouble(1, rate);
            statement.setString(2, baseCode);
            statement.setString(3, targetCode);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRates> findByCodes(String substring, String substring1) {
        try(  Connection connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_BY_ID_CODES_SQL)){
            List<ExchangeRates> result = convertResultSetToList(statement.executeQuery());
            return Optional.ofNullable(result.size() == 1 ? result.get(0) : null);

        }
        catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    @Override
    public Optional<ExchangeRates> findByCodesAndReverse(String baseCurCode, String targetCurCode) {
        try(Connection connection = ConnectionManager.get();
              var statement = connection.prepareStatement(FIND_BY_CODES_REVERSE_SQL)){
            statement.setString(1, baseCurCode);
            statement.setString(2, targetCurCode);
            statement.setString(3, targetCurCode);
            statement.setString(4, baseCurCode);
            List<ExchangeRates> result = convertResultSetToList(statement.executeQuery());
            return Optional.ofNullable(result.size() == 1 ? result.get(0) : null);

        }
        catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }


    // todo даныне методы не пригодились в проекте, поэтому не стал их реализовывать )

    // todo impl
    @Override
    public ExchangeRates save(ExchangeRates entity) {
        return entity;
    }

    //todo impl
    @Override
    public ExchangeRates update(ExchangeRates entity) {
        return null;
    }

    //todo impl

    @Override
    public void delete(Long id) {

    }

    // todo impl
    @Override
    public Optional<ExchangeRates> findById(Long id) {
        return null;
    }


}
