package com.example.userservice.sercice;

import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с пользователями. Используется для запросов с консоли
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    /**
     * Конструктор с внедрением зависимости UserRepository.
     *
     * @param userRepository репозиторий для работы с пользователями
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Создаёт нового пользователя.
     *
     * @param userDto данные пользователя для создания
     * @return созданный пользователь в формате DTO
     */
    @Transactional
    public UserDto createUser(UserDto userDto) {
        logger.info("Создание нового пользователя: {}", userDto.getName());
        UserEntity userEntity = new UserEntity(userDto.getName(), userDto.getEmail(), userDto.getAge());
        UserEntity savedUserEntity = userRepository.save(userEntity);
        logger.debug("Пользователь успешно создан с ID: {}", savedUserEntity.getId());
        return convertToDto(savedUserEntity);
    }

    /**
     * Получает список всех пользователей.
     *
     * @return список пользователей в формате DTO
     */
    public List<UserDto> getAllUsers() {
        logger.info("Запрос на получение всех пользователей");
        List<UserEntity> users = userRepository.findAll();
        logger.debug("Найдено пользователей: {}", users.size());
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получает пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return пользователь в формате DTO
     */
    public UserDto getUserById(Long id) {
        logger.info("Запрос на получение пользователя с ID: {}", id);
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        logger.debug("Найден пользователь: ID={}, имя={}", id, userEntity.getName());
        return convertToDto(userEntity);
    }

    /**
     * Обновляет существующего пользователя.
     *
     * @param id      идентификатор пользователя для обновления
     * @param userDto обновлённые данные пользователя
     * @return обновлённый пользователь в формате DTO
     */
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        logger.info("Обновление пользователя с ID: {}", id);
        UserEntity existingUserEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        existingUserEntity.setName(userDto.getName());
        existingUserEntity.setEmail(userDto.getEmail());
        existingUserEntity.setAge(userDto.getAge());

        UserEntity updatedUserEntity = userRepository.save(existingUserEntity);
        logger.info("Пользователь с ID {} успешно обновлён", id);
        return convertToDto(updatedUserEntity);
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param id идентификатор пользователя для удаления
     */
    @Transactional
    public void deleteUser(Long id) {
        logger.info("Запрос на удаление пользователя с ID: {}", id);
        if (!userRepository.existsById(id)) {
            logger.warn("Попытка удаления несуществующего пользователя с ID: {}", id);
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        logger.info("Пользователь с ID {} успешно удалён", id);
    }

    /**
     * Преобразует сущность UserEntity в DTO UserDto.
     *
     * @param userEntity сущность пользователя
     * @return DTO пользователя
     */
    private UserDto convertToDto(UserEntity userEntity) {
        return new UserDto(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getAge(),
                userEntity.getCreatedAt()
        );
    }
}