package com.example.userservice.sercice;

import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    /**
     * Успешное создание пользователя.
     */
    @Test
    void createUser_shouldSaveUser() {
        UserDto dto = new UserDto(null, "Ivan", "ivan@mail.ru", 25);
        UserEntity entity = new UserEntity("Ivan", "ivan@mail.ru", 25);

        when(userRepository.save(any(UserEntity.class))).thenReturn(entity);

        UserDto result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals("Ivan", result.getName());
        verify(userRepository).save(any(UserEntity.class));
    }

    /**
     * Получение пользователя по ID.
     */
    @Test
    void getUserById_shouldReturnUser() {
        UserEntity entity = new UserEntity("Ivan", "ivan@mail.ru", 25);
        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        UserDto result = userService.getUserById(1L);

        assertEquals("Ivan", result.getName());
    }

    /**
     * Ошибка, если пользователь не найден по ID.
     */
    @Test
    void getUserById_shouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

    /**
     * Удаление существующего пользователя.
     */
    @Test
    void deleteUser_shouldDeleteWhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    /**
     * Ошибка при удалении несуществующего пользователя.
     */
    @Test
    void deleteUser_shouldThrowExceptionWhenNotExists() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(99L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}