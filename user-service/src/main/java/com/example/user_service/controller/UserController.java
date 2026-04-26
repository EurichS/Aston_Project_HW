package com.example.user_service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.common_models.exception.UserNotFoundException;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Validated
@Tag(name = "User Management", description = "API для управления пользователями")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private UserService userService;

    @PostMapping
    @Operation(summary = "Создать нового пользователя", description = "Создаёт нового пользователя в системе")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные пользователя")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        createdUser.add(linkTo(methodOn(UserController.class).getUserById(createdUser.getId())).withSelfRel());
        createdUser.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
        createdUser.add(linkTo(methodOn(UserController.class).updateUser(createdUser.getId(), userDTO)).withRel("update"));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID", description = "Возвращает информацию о пользователе по его уникальному идентификатору")
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") @NotNull @Positive Long id) {
        Optional<UserDTO> user = userService.findUserById(id);
        if (user.isPresent()) {
            UserDTO userDto = user.get();
            userDto.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
            userDto.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("collection"));
            userDto.add(linkTo(methodOn(UserController.class).updateUser(id, userDto)).withRel("update"));
            userDto.add(linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"));
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей в системе")
    @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        users.forEach(user -> {
            user.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withRel("self"));
            user.add(linkTo(methodOn(UserController.class).updateUser(user.getId(), user)).withRel("update"));
        });
        if (!users.isEmpty()) {
            users.get(0).add(linkTo(methodOn(UserController.class).createUser(new UserDTO())).withRel("create"));
        }
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя", description = "Обновляет данные существующего пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь обновлён")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @ApiResponse(responseCode = "400", description = "Некорректные данные для обновления")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") @NotNull @Positive Long id,
                                              @RequestBody @Valid UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            updatedUser.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
            updatedUser.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("collection"));
            updatedUser.add(linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"));
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя из системы по его ID")
    @ApiResponse(responseCode = "204", description = "Пользователь удалён")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public void deleteUser(@PathVariable("id") @NotNull @Positive Long id) throws UserNotFoundException {
        userService.deleteUser(id);
    }
}

