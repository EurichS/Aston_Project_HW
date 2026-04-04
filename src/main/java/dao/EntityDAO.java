package dao;

import entity.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * @ Абстракция с CRUD-операциями
 * и отображением всей БД.
 */
public interface EntityDAO {
    void create(UserEntity userEntity);

    Optional<UserEntity> read(Long id);

    void update(UserEntity userEntity);

    void remove(Long id);

    List<UserEntity> findAll();
}
