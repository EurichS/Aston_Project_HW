package dao;

import entity.UserEntity;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * @ Класс с логикой взаимодействия с БД:
 * CRUD-методы+ отображение всей БД
 */
public class UserDao implements EntityDAO {
    private static final Logger logger = LoggerFactory.getLogger(EntityDAO.class);

    /**
     * @ Method Name: create
     * @ Description: создание юзера в БД.
     * Поскольку поле email юзера должно
     * быть уникально, в случае неудачи создания
     * проверяем instanceof исключения: если true-
     * значит email занят, о чем сообщаем логгером
     */
    @Override
    public void create(UserEntity userEntity) {
        Transaction transaction = null;
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(userEntity);
            transaction.commit();
            logger.info("Пользователь успешно создан: {}", userEntity);
        } catch (PersistenceException e) { // Hibernate оборачивает ошибки БД в PersistenceException
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            // Проверяем на дубликаты (Unique Constraint)
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                String constraintName = ((org.hibernate.exception.ConstraintViolationException) e.getCause()).getConstraintName();
                logger.error("Ошибка уникальности данных (ограничение: {}): {}", constraintName, e.getMessage());

                throw new IllegalArgumentException(
                        "Ошибка сохранения: данные уже существуют или нарушают правила системы", e);
            }

            logger.error("Ошибка уровня Persistence при создании пользователя", e);
            throw e;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Непредвиденная ошибка при создании пользователя: ", e);
            throw e;
        }
    }

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

    @Override
    public List<UserEntity> findAll() {
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            return session.createQuery("FROM UserEntity", UserEntity.class).getResultList();
        } catch (Exception e) {
            logger.error("Ошибка при получении всех пользователей: ", e);
            throw e;
        }
    }

    @Override
    public void update(UserEntity userEntity) {
        Transaction transaction = null;
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(userEntity);
            transaction.commit();
            logger.info("Пользователь успешно обновлён: {}", userEntity);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при обновлении пользователя: ", e);
            throw e;
        }
    }

    @Override
    public void remove(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateSetup.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            UserEntity userEntity = session.get(UserEntity.class, id);
            if (userEntity != null) {
                session.remove(userEntity);
                logger.info("Пользователь с ID {} успешно удалён", id);
            } else {
                logger.warn("Попытка удаления несуществующего пользователя с ID {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при удалении пользователя с ID {}: ", id, e);
            throw e;
        }
    }
}
