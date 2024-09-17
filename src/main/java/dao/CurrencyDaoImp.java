package dao;

import dto.CurrencyDto;
import entity.Currency;
import exception.UniqueException;
import utils.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
            e.printStackTrace();
            throw new RuntimeException("Failed to load PostgreSQL JDBC driver", e);
        }
    }


    @Override
    public List<CurrencyDto> findAll() {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet result = statement.executeQuery();
            ArrayList<CurrencyDto> currencies = new ArrayList<>();
            while (result.next()){
                currencies.add(CurrencyDto
                        .builder()
                        .sign(result.getString("sign"))
                        .code(result.getString("code"))
                        .fullName(result.getString("full_name")).build());
            }
            return currencies;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CurrencyDto findByCode(String currencyCode) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            statement.setString(1, currencyCode);
            var result = statement.executeQuery();
            if (result.next()){
                return CurrencyDto
                        .builder()
                        .sign(result.getString("sign"))
                        .fullName(result.getString("full_name"))
                        .code(result.getString("code")).build();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Currency findByCodeWithId(String currencyCode) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            statement.setString(1, currencyCode);
            var result = statement.executeQuery();
            if (result.next()){
                return Currency
                        .builder()
                        .sign(result.getString("sign"))
                        .fullName(result.getString("full_name"))
                        .code(result.getString("code"))
                        .id(result.getLong("id"))
                        .build();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
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
            throw new RuntimeException();



        } catch (SQLException e) {
            throw new UniqueException(e);
        }
    }

    @Override
    public CurrencyDto findById(long id) {
        try (var connection = ConnectionManager.get();
        var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            var result = statement.executeQuery();
            if (result.next()){
                return CurrencyDto
                        .builder()
                        .sign(result.getString("sign"))
                        .fullName(result.getString("full_name"))
                        .code(result.getString("code")).build();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Currency> findByLikeCode(String c) {
        try (var con = ConnectionManager.get();
            var statement = con.prepareStatement(FIND_BY_CODE_LIKE_SQL)){
            String pattern = c + "%";
            statement.setString(1, pattern);
            var result = statement.executeQuery();
            List<Currency> cur = new ArrayList<>();
            while (result.next()){
                cur.add(Currency
                        .builder()
                        .id(result.getLong("id"))
                        .sign(result.getString("sign"))
                        .code(result.getString("code"))
                        .fullName(result.getString("full_name")).build());
            }
            return cur;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
