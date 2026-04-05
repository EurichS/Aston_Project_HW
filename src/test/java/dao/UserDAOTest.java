package dao;

import entity.UserEntity;
import hibernate.HibernateSetup;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестовый класс для проверки функциональности {@link UserDAO}.
 * <p>
 * Использует Testcontainers для запуска PostgreSQL в Docker‑контейнере во время тестирования.
 * После каждого теста выполняется очистка базы данных.
 */
@Testcontainers
class UserDAOTest {

    /**
     * Контейнер PostgreSQL, запускаемый через Testcontainers.
     * Используется для изоляции тестовой среды и гарантированной чистоты данных.
     */
    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15");

    /**
     * Экземпляр {@link UserDAO}, функциональность которого тестируется.
     */
    UserDAO userDAO = new UserDAO();

    /**
     * Очищает базу данных после выполнения каждого теста.
     * <p>
     * Выполняет SQL‑запрос <code>DELETE FROM UserEntity</code> для удаления всех записей
     * из таблицы пользователей, обеспечивая изоляцию тестов друг от друга.
     */
    @AfterEach
    void tearDown() {
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM UserEntity").executeUpdate();
            tx.commit();
        }
    }

    /**
     * Тест создания пользователя.
     * <p>
     * Проверяет, что метод {@link UserDAO#create(UserEntity)} корректно сохраняет
     * сущность в БД и возвращает её с установленным ID.
     */
    @Test
    void create_CreateUserEntity_EqualsUser() {
        UserEntity userEntity = new UserEntity("TestName", "test@yest.com", 50);

        Optional<UserEntity> userInput = userDAO.create(userEntity);

        assertEquals(userEntity, userInput.get());
    }

    /**
     * Тест чтения существующего пользователя по корректному ID.
     * <p>
     *  Создаёт пользователя через {@link UserDAO#create(UserEntity)}.
     *  Получает ID созданного пользователя.
     *  Читает пользователя по этому ID через {@link UserDAO#read(Long)}.
     *  Проверяет, что возвращённая сущность совпадает с исходной.
     */
    @Test
    void read_ReadUserEntityCorrectId_OptionalUserEntity() {
        UserEntity userEntity = new UserEntity("TestName", "test@yest.com", 50);
        Long id = userDAO.create(userEntity).get().getId();

        Optional<UserEntity> userInput = userDAO.read(id);

        assertEquals(userEntity, userInput.get());
    }

    /**
     * Тест чтения пользователя с некорректным отрицательным ID.
     * <p>
     * Проверяет, что {@link UserDAO#read(Long)} возвращает пустой {@link Optional}
     * для несуществующего отрицательного ID.
     */
    @Test
    void read_ReadUserEntityInCorrectId_OptionalNull() {
        Long idOutput = -2L;

        Optional<UserEntity> userInput = userDAO.read(idOutput);

        assertTrue(userInput.isEmpty());
    }

    /**
     * Тест чтения несуществующего пользователя.
     * <p>
     * Проверяет, что {@link UserDAO#read(Long)} возвращает пустой {@link Optional}
     * при запросе пользователя с ID, которого нет в БД.
     */
    @Test
    void read_UserNotFound_OptionalUserEntity() {
        Long idOutput = 2000L;

        Optional<UserEntity> userInput = userDAO.read(idOutput);

        assertTrue(userInput.isEmpty());
    }

    /**
     * Тест получения всех пользователей.
     * <p>
     * Проверяет, что {@link UserDAO#findAll()} возвращает коллекцию (реализацию {@link Collection}),
     * даже если в БД нет записей.
     */
    @Test
    void findAll_getBD_ListUserEntity() {
        assertTrue(userDAO.findAll() instanceof Collection);
    }

    /**
     * Тест обновления существующего пользователя.
     * <p>
     *  Создаёт пользователя.
     *  Создаёт новую сущность с обновлёнными данными и тем же ID.
     *  Вызывает {@link UserDAO#update(UserEntity)}.
     *  Проверяет, что обновлённая сущность совпадает с переданной на обновление.
     */
    @Test
    void update_CorrectId_OptionalUserEntity() {
        UserEntity userEntity = new UserEntity("Maxic", "test@yeaast.com", 30);
        Long id = userDAO.create(userEntity).get().getId();

        UserEntity userEntityInput = new UserEntity("Maximus", "test@yeaassst.com", 31);
        userEntityInput.setId(id);
        Optional<UserEntity> userOutput = userDAO.update(userEntityInput);

        assertEquals(userEntityInput, userOutput.get());
    }

    /**
     * Тест удаления существующего пользователя.
     * <p>
     *  Создаёт пользователя.
     *  Удаляет его по ID через {@link UserDAO#remove(Long)}.
     *  Проверяет, что удалённый пользователь совпадает с изначально созданным.
     */
    @Test
    void remove_CorrectId_OptionalUserEntity() {
        UserEntity userEntity = new UserEntity("Al", "tesdst@yeaast.com", 13);
        Long id = userDAO.create(userEntity).get().getId();

        Optional<UserEntity> userOutput = userDAO.remove(id);

        assertEquals(userEntity, userOutput.get());
    }
}