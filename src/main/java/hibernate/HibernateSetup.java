package hibernate;

import entity.UserEntity;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Утилитарный класс для настройки и управления жизненным циклом {@link SessionFactory}.
 * <p>
 * Обеспечивает создание единственного экземпляра @see sessionFactory
 * и предоставляет к нему глобальную точку доступа.
 */
@NoArgsConstructor
public class HibernateSetup {
    private static final Logger logger = LoggerFactory.getLogger(HibernateSetup.class);
    private static SessionFactory sessionFactory;

    /**
     * Возвращает сконфигурированный экземпляр {@link SessionFactory}.
     * <p>
     * При первом вызове инициализирует фабрику, используя стандартный файл
     * конфигурации и регистрируя сущность {@link UserEntity}.
     *
     * @return текущий экземпляр {@link SessionFactory} или {@code null} в случае ошибки инициализации.
     */

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(UserEntity.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
                logger.info("Hibernate SessionFactory created successfully");
            } catch (Exception e) {
                logger.error("Failed to create SessionFactory: {}", e.getMessage());
                if (sessionFactory != null) {
                    sessionFactory.close();
                }
            }
        }
        return sessionFactory;
    }
}