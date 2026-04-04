package dao;

import entity.UserEntity;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


public class UserDao implements EntityDAO {
    private static final Logger logger = LoggerFactory.getLogger(EntityDAO.class);
    /**
     * Сохраняет новую сущность пользователя в базе данных.
     * <p>
     * Операция выполняется в рамках транзакции. Если при сохранении происходит
     * нарушение ограничений уникальности (например, дубликат email), транзакция
     * откатывается, и выбрасывается {@link IllegalArgumentException}.
     * При возникновении иных системных ошибок транзакция также откатывается,
     * а исключение пробрасывается выше.
     *
     * @param userEntity объект пользователя для сохранения.
     * @return {@link Optional}, содержащий сохраненную сущность.
     * @throws IllegalArgumentException если данные нарушают ограничения БД.
     * @throws PersistenceException если возникла ошибка на уровне Hibernate/JPA.
     * @throws RuntimeException при возникновении непредвиденных системных ошибок.
     */
    @Override
    public Optional<UserEntity> create(UserEntity userEntity) {
        Transaction transaction = null;
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(userEntity);
            transaction.commit();
            logger.info("Пользователь успешно создан: {}", userEntity);
            return Optional.of(userEntity);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            if (e instanceof PersistenceException && e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                String constraint = ((org.hibernate.exception.ConstraintViolationException) e.getCause()).getConstraintName();
                logger.error("Нарушение ограничений БД ({}): {}", constraint, e.getMessage());
                throw new IllegalArgumentException("Данные нарушают правила базы данных", e);
            }
            logger.error("Критическая ошибка при создании пользователя", e);
            throw e;
        }
    }
    /**
     * Выполняет поиск пользователя в базе данных по его уникальному идентификатору.
     * <p>
     *  Если пользователь с указанным ID не найден, возвращается пустой {@link Optional}.
     *
     * @param id уникальный идентификатор пользователя.
     * @return {@link Optional}, содержащий найденную сущность,
     * или пустой {@link Optional}, если запись отсутствует.
     * @throws RuntimeException если возникла ошибка при выполнении запроса к базе данных.
     */
    @Override
    public Optional<UserEntity> read(Long id) {
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            UserEntity userEntity = session.get(UserEntity.class, id);
            return Optional.ofNullable(userEntity);
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя по ID: ", e);
            throw e;
        }
    }
    /**
     * Извлекает из базы данных полный список всех пользователей.
     * <p>
     * Метод выполняет HQL-запрос для получения всех записей сущности {@link UserEntity}.
     * Если в таблице нет данных, возвращается пустой список.
     *
     * @return список объектов {@link UserEntity}; никогда не возвращает {@code null}.
     * @throws RuntimeException если возникла ошибка при выполнении запроса к базе данных.
     */
    @Override
    public List<UserEntity> findAll() {
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            return session.createQuery("FROM UserEntity", UserEntity.class).getResultList();
        } catch (Exception e) {
            logger.error("Ошибка при получении всех пользователей: ", e);
            throw e;
        }
    }
    /**
     * Обновляет данные существующего пользователя в базе данных.
     * <p>
     * Операция выполняется в рамках транзакции с использованием метода {@code merge}.
     * Это позволяет обновить состояние сущности и перезаписать ее
     *  В случае ошибки транзакция откатывается.
     *
     * @param userEntity объект пользователя с обновленными данными.
     *                   Поле ID должно соответствовать существующей записи в БД.
     * @return {@link Optional}, содержащий обновленный экземпляр сущности.
     * @throws PersistenceException если возникла ошибка при обновлении или слиянии данных.
     * @throws RuntimeException при непредвиденных системных ошибках.
     */
    @Override
    public Optional<UserEntity> update(UserEntity userEntity) {
        Transaction transaction = null;
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            UserEntity updatedUser = session.merge(userEntity);
            transaction.commit();
            logger.info("Пользователь успешно обновлён: {}", updatedUser);
            return Optional.of(updatedUser);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Ошибка при обновлении пользователя: ", e);
            throw e;
        }
    }
    /**
     * Удаляет запись пользователя из базы данных по его идентификатору.
     * <p>
     * Перед удалением выполняется поиск сущности. Если пользователь с указанным
     * ID найден, он удаляется в рамках транзакции. Если запись отсутствует,
     * операция завершается без ошибок, возвращая пустой результат.
     *
     * @param id уникальный идентификатор пользователя, которого необходимо удалить.
     * @return {@link Optional}, содержащий данные удаленного пользователя,
     *         или пустой {@link Optional}, если пользователь не был найден.
     * @throws PersistenceException если возникла ошибка при выполнении операции удаления.
     * @throws RuntimeException при непредвиденных системных ошибках.
     */
    @Override
    public Optional<UserEntity> remove(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            UserEntity userEntity = session.get(UserEntity.class, id);
            Optional<UserEntity> result = Optional.ofNullable(userEntity);
            if (userEntity != null) {
                session.remove(userEntity);
                logger.info("Пользователь с ID {} успешно удалён", id);
            } else {
                logger.warn("Попытка удаления несуществующего пользователя с ID {}", id);
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Ошибка при удалении пользователя с ID {}: ", id, e);
            throw e;
        }
    }
}
