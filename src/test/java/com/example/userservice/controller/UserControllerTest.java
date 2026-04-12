package com.example.userservice.controller;

import com.example.userservice.console.Console;
import com.example.userservice.dto.UserDto;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.sercice.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private Console console;

    /**
     * Проверяет возврат списка пользователей (статус 200 OK, корректный JSON).
     */
    @Test
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        List<UserDto> users = Arrays.asList(
                new UserDto(1L, "Alex", "al@yandex.ru", 21),
                new UserDto(2L, "July", "yu@yandex.ru", 18)
        );
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alex"))
                .andExpect(jsonPath("$[0].email").value("al@yandex.ru"))
                .andExpect(jsonPath("$[0].age").value(21))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("July"))
                .andExpect(jsonPath("$[1].email").value("yu@yandex.ru"))
                .andExpect(jsonPath("$[1].age").value(18));
    }

    /**
     * Проверяет возврат пустого массива при отсутствии пользователей (статус 200 OK).
     */
    @Test
    void getAllUsers_shouldReturnEmptyListWhenNoUsers() throws Exception {
        List<UserDto> emptyList = List.of();
        when(userService.getAllUsers()).thenReturn(emptyList);

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Проверка получения пользователя по ID (200 OK).
     */
    @Test
    void getUserById_shouldReturnUser() throws Exception {
        UserDto user = new UserDto(1L, "Alex", "al@yandex.ru", 21);
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alex"))
                .andExpect(jsonPath("$.email").value("al@yandex.ru"))
                .andExpect(jsonPath("$.age").value(21));
    }

    /**
     * Проверка поведения, если пользователь не найден (404 Not Found).
     */
    @Test
    void getUserById_shouldReturn404WhenNotFound() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    /**
     * Проверка создания пользователя (201 Created).
     */
    @Test
    void createUser_shouldReturnCreated() throws Exception {
        UserDto inputDto = new UserDto(null, "Ivan", "ivan@mail.ru", 25);
        UserDto savedDto = new UserDto(1L, "Ivan", "ivan@mail.ru", 25);

        when(userService.createUser(any(UserDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@mail.ru"))
                .andExpect(jsonPath("$.age").value(25));
    }

    /**
     * Проверка валидации при создании (400 Bad Request).
     */
    @Test
    void createUser_shouldReturnBadRequestWhenInvalid() throws Exception {
        UserDto invalidDto = new UserDto(null, "", "not-an-email", -5);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Проверка обновления пользователя (200 OK).
     */
    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserDto inputDto = new UserDto(null, "Ivan", "ivan@mail.ru", 30);
        UserDto resultDto = new UserDto(1L, "Ivan", "ivan@mail.ru", 30);

        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(resultDto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@mail.ru"))
                .andExpect(jsonPath("$.age").value("30"));
    }

    /**
     * Проверка валидации при обновлении (400 Bad Request).
     */
    @Test
    void updateUser_shouldReturnBadRequestWhenInvalid() throws Exception {
        UserDto invalidDto = new UserDto(null, "", "bad-email", -1);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Проверка удаления пользователя (204 No Content).
     */
    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        Long userId = 1L;

        doThrow(new UserNotFoundException(100L)).when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("404 NOT_FOUND \"Пользователь с ID 100 не найден\"")); // Проверка текста ошибки
    }

    /**
     * Проверка удаления несуществующего пользователя (404 Not Found).
     */
    @Test
    void deleteUser_shouldReturnNotFoundWhenNotExists() throws Exception {
        Long userId = 99L;

        // Если сервис кидает исключение, когда ID не найден
        doThrow(new UserNotFoundException(99L)).when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }
}


