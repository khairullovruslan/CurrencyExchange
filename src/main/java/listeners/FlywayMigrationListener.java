package listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.flywaydb.core.Flyway;
import utils.PropertiesUtil;

@WebListener
public class FlywayMigrationListener implements ServletContextListener {
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        String url = PropertiesUtil.get(URL_KEY);
        String user =  PropertiesUtil.get(USERNAME_KEY);
        String password =  PropertiesUtil.get(PASSWORD_KEY);

        Flyway flyway = Flyway.configure()
                .dataSource(url, user, password)
                .load();
        flyway.migrate();
        System.out.println("Миграции выполнены успешно!");
    }


}
