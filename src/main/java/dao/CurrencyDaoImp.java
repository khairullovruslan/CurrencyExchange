package dao;

import entity.Currency;
import exception.DaoException;
import exception.UniqueException;
import utils.ConnectionManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDaoImp implements CurrencyDao{
    private final String FIND_ALL_SQL = "select * from  Currencies";
    private final String FIND_BY_CODE_SQL = "select * from  Currencies where code = ?";
    private final String SAVE_SQL = "insert into currencies(code, full_name, sign)" +
            "values (?, ? , ?)";
    private final String FIND_BY_ID_SQL = "select * from  Currencies where id = ?";
    private final String FIND_BY_CODE_LIKE_SQL = "select * from  Currencies where code like ? ";


    private final static CurrencyDao INSTANCE = new CurrencyDaoImp();

    public static CurrencyDao getInstance() {

        return INSTANCE;
    }

    private CurrencyDaoImp(){

    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load PostgreSQL JDBC driver", e);
        }
    }

    private List<Currency> convertResultSetToList(ResultSet result) throws SQLException {
        ArrayList<Currency> currencies = new ArrayList<>();
        while (result.next()){
            currencies.add(Currency
                    .builder()
                    .id(result.getLong("id"))
                    .sign(result.getString("sign"))
                    .code(result.getString("code"))
                    .fullName(result.getString("full_name")).build());
        }
        return currencies;
    }



    @Override
    public List<Currency> findAll() {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            return convertResultSetToList(statement.executeQuery());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Currency> findByCode(String currencyCode) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            statement.setString(1, currencyCode);
            var result = statement.executeQuery();
            List<Currency> currencies = convertResultSetToList(result);
            return Optional.ofNullable(currencies.size() == 1 ? currencies.get(0) : null);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Currency save(Currency currency) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, currency.getCode());
            statement.setObject(2, currency.getFullName());
            statement.setObject(3, currency.getSign());
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if (keys.next()){
                currency.setId(keys.getLong("id"));
                return currency;
            }
            throw new DaoException();



        } catch (SQLException e) {
            throw new UniqueException(e);
        }
    }

    // todo impl
    @Override
    public Currency update(Currency entity) {
        return null;
    }

    // todo impl
    @Override
    public void delete(Long id) {

    }


    @Override
    public Optional<Currency> findById(Long id) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            List<Currency> currencies = convertResultSetToList(statement.executeQuery());
            return Optional.ofNullable(currencies.size() == 1 ? currencies.get(0) : null);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Currency> findByLikeCode(String c) {
        try (var con = ConnectionManager.get();
             var statement = con.prepareStatement(FIND_BY_CODE_LIKE_SQL)){
            String pattern = c + "%";
            statement.setString(1, pattern);
            return convertResultSetToList(statement.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
