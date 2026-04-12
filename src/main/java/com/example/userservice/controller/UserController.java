package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.sercice.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер для управления пользователями.
 * Обрабатывает HTTP‑запросы по пути /api/users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * Конструктор с внедрением зависимости UserService.
     *
     * @param userService сервис для работы с пользователями
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Получает список всех пользователей.
     *
     * @return ResponseEntity со списком UserDto и статусом 200 OK
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Получает пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return ResponseEntity с UserDto и статусом 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    /**
     * Создаёт нового пользователя.
     *
     * @param userDto данные пользователя для создания (с валидацией)
     * @return ResponseEntity с созданным UserDto, статусом 201 Created и заголовком Location
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Обновляет существующего пользователя.
     *
     * @param id      идентификатор пользователя для обновления
     * @param userDto обновлённые данные пользователя (с валидацией)
     * @return ResponseEntity с обновлённым UserDto и статусом 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param id идентификатор пользователя для удаления
     * @return ResponseEntity без тела и со статусом 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}