package utils;


import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;

public class ConnectionManager {
    private static final ConnectionManager INSTANCE = new ConnectionManager();
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static ArrayBlockingQueue<Connection> pool;


    static {

        try {
            initConnectionPool();
        } catch (Exception e) {
            System.out.println("error connection");
        }

    }

    private static void initConnectionPool() {
        String poolSize = PropertiesUtil.get(POOL_SIZE_KEY);
        int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);

        pool = new ArrayBlockingQueue<>(size);
        System.out.println("connection added in pool...");
        for (int i = 0; i < size; i++) {
            Connection connection = open();
            ClassLoader connectionClassLoader = connection.getClass().getClassLoader();
            var proxyConnection = Proxy.newProxyInstance(connectionClassLoader,
                    new Class[]{Connection.class},
                    ((proxy, method, args) ->
                            method.getName().equals("close") ? pool.add((Connection) proxy) : method.invoke(connection, args
                            )));
            pool.offer((Connection) proxyConnection);
        }

    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(PropertiesUtil.get(URL_KEY), PropertiesUtil.get(USERNAME_KEY), PropertiesUtil.get(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static int size() {
        return pool.size();
    }

    private ConnectionManager() {

    }
}
