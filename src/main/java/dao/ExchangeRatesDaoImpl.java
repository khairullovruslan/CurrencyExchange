package dao;

import entity.Currency;
import entity.ExchangeRates;
import exception.UniqueException;
import utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ExchangeRatesDaoImpl implements ExchangeRatesDao {
    private final String SELECT_TEMPLATE =
            """
                    select currency.id as cur1Id, currency.code as cur1Code, currency.full_name as cur1Name, currency.sign as cur1Sign,
                    currency2.id as cur2Id, currency2.code as cur2Code, currency2.full_name as cur2Name, currency2.sign as cur2Sign,
                    exchange.id as exchangeId, rate from exchangerates as exchange
                    """;

    private final String RETURNING_TEMPLATE =
            """
                    RETURNING
                    exchangerates.id as exchangeId,
                    exchangerates.rate,
                    (SELECT cur1.id as cur1Id FROM currencies AS cur1 WHERE cur1.code = ?),
                    (SELECT cur2.id as cur2Id  FROM currencies AS cur2 WHERE cur2.code = ?),
                    (SELECT cur1.sign as cur1Sign FROM currencies AS cur1 WHERE cur1.code = ?),
                    (SELECT cur2.sign as cur2Sign FROM currencies AS cur2 WHERE cur2.code = ?),
                    (SELECT cur1.full_name as cur1Name FROM currencies AS cur1 WHERE cur1.code = ?),
                    (SELECT cur2.full_name as cur2Name FROM currencies AS cur2 WHERE cur2.code = ?),
                    (SELECT cur1.code as cur1Code  FROM currencies AS cur1 WHERE cur1.code = ?),
                    (SELECT cur2.code as cur2Code FROM currencies AS cur2 WHERE cur2.code = ?);
                     
                    """;

    private final String FIND_ALL_SQL = SELECT_TEMPLATE +

            """
                        join currencies as currency on exchange.BaseCurrencyId = currency.id
                        join currencies as currency2 on exchange.targetcurrencyid = currency2.id;
                    """;

    private final String FIND_BY_CODES_SQL = SELECT_TEMPLATE +

            """
                    join currencies as currency on exchange.BaseCurrencyId = currency.id
                    join currencies as currency2 on exchange.targetcurrencyid = currency2.id where currency.code = ? and currency2.code = ?
                    """;

    private final String FIND_BY_CODES_REVERSE_SQL = SELECT_TEMPLATE +
            """
                    join currencies as currency on exchange.BaseCurrencyId = currency.id
                    join currencies as currency2 on exchange.targetcurrencyid = currency2.id
                    where currency.code = ? and currency2.code = ? or currency.code = ? and currency2.code = ?
                    """;

    private final String SAVE_SQL = """
                  
                INSERT INTO exchangerates(basecurrencyid, targetcurrencyid, rate)
                VALUES (
                    (SELECT id FROM currencies AS cur1 WHERE cur1.code = ?),
                    (SELECT id FROM currencies AS cur2 WHERE cur2.code = ?),
                    ?
                )
            """ + RETURNING_TEMPLATE;

    private final String UPDATE_SQl =

            """
                    UPDATE exchangerates
                    SET rate = ?
                    FROM currencies AS cur1, currencies AS cur2
                    WHERE exchangerates.BaseCurrencyId = cur1.id
                      AND exchangerates.TargetCurrencyId = cur2.id
                      AND cur1.code = ?
                      AND cur2.code = ?
                    """ + RETURNING_TEMPLATE;


    public final static ExchangeRatesDaoImpl INSTANCE = new ExchangeRatesDaoImpl();

    public static ExchangeRatesDaoImpl getInstance() {
        return INSTANCE;
    }

    private ExchangeRatesDaoImpl() {

    }

    private List<ExchangeRates> convertResultSetToList(ResultSet resultSet) throws SQLException {
        ArrayList<ExchangeRates> result = new ArrayList<>();

        while (resultSet.next()) {
            Currency baseCur = Currency
                    .builder()
                    .id(resultSet.getLong("cur1id"))
                    .code(resultSet.getString("cur1code"))
                    .fullName(resultSet.getString("cur1name"))
                    .sign(resultSet.getString("cur1sign"))
                    .build();

            Currency targetCur = Currency
                    .builder()
                    .id(resultSet.getLong("cur2id"))
                    .code(resultSet.getString("cur2code"))
                    .fullName(resultSet.getString("cur2name"))
                    .sign(resultSet.getString("cur2sign"))
                    .build();

            result.add(ExchangeRates
                    .builder()
                    .id(resultSet.getLong("exchangeid"))
                    .baseCurrency(baseCur).targetCurrency(targetCur).rate(resultSet.getDouble("rate")).build());
        }
        return result;
    }


    @Override
    public List<ExchangeRates> findAll() {
        try (Connection con = ConnectionManager.get();
             var statement = con.prepareStatement(FIND_ALL_SQL)) {
            return convertResultSetToList(statement.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<ExchangeRates> save(String baseCode, String targetCode, Double rate) {
        try (Connection connection = ConnectionManager.get();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, baseCode);
            statement.setString(2, targetCode);
            statement.setDouble(3, rate);
            statement.setString(4, baseCode);
            statement.setString(5, targetCode);
            statement.setString(6, baseCode);
            statement.setString(7, targetCode);
            statement.setString(8, baseCode);
            statement.setString(9, targetCode);
            statement.setString(10, baseCode);
            statement.setString(11, targetCode);
            statement.executeUpdate();
            List<ExchangeRates> rates = convertResultSetToList(statement.getGeneratedKeys());
            return Optional.ofNullable(rates.size() == 1 ? rates.get(0) : null);

        } catch (SQLException e) {
            throw new UniqueException(e);
        }
    }

    @Override
    public Optional<ExchangeRates> changeRate(String baseCode, String targetCode, Double rate) {
        try (Connection connection = ConnectionManager.get();
             var statement = connection.prepareStatement(UPDATE_SQl, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDouble(1, rate);
            statement.setString(2, baseCode);
            statement.setString(3, targetCode);
            statement.setString(4, baseCode);
            statement.setString(5, targetCode);
            statement.setString(6, baseCode);
            statement.setString(7, targetCode);
            statement.setString(8, baseCode);
            statement.setString(9, targetCode);
            statement.setString(10, baseCode);
            statement.setString(11, targetCode);
            statement.executeUpdate();


            List<ExchangeRates> exchangeRates = convertResultSetToList(statement.getGeneratedKeys());
            System.out.println(exchangeRates);
            return Optional.ofNullable(exchangeRates.size() == 1 ? exchangeRates.get(0) : null);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRates> findByCodes(String substring, String substring1) {
        try (Connection connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_BY_CODES_SQL)) {
            statement.setString(1, substring);
            statement.setString(2, substring1);
            List<ExchangeRates> result = convertResultSetToList(statement.executeQuery());
            return Optional.ofNullable(result.size() == 1 ? result.get(0) : null);

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    @Override
    public Optional<ExchangeRates> findByCodesAndReverse(String baseCurCode, String targetCurCode) {
        try (Connection connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_BY_CODES_REVERSE_SQL)) {
            statement.setString(1, baseCurCode);
            statement.setString(2, targetCurCode);
            statement.setString(3, targetCurCode);
            statement.setString(4, baseCurCode);
            List<ExchangeRates> result = convertResultSetToList(statement.executeQuery());
            return Optional.ofNullable(result.size() == 1 ? result.get(0) : null);

        } catch (SQLException e) {
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
    public Optional<Currency> findById(Long id) {
        return null;
    }


}
