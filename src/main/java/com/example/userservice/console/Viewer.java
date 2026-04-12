package com.example.userservice.console;

import com.example.userservice.dto.UserDto;

import java.util.List;

public class Viewer {
    public void showMenu() {
        System.out.println("\n=== Управление пользователями ===");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Показать всех пользователей");
        System.out.println("3. Получить пользователя по ID");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    public void showAllUsers(List<UserDto> users) {
        System.out.println("\n--- Список пользователей ---");
        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены.");
        } else {
            users.forEach(user -> System.out.printf("ID: %d, Имя: %s, Email: %s, Возраст: %d\n",
                    user.getId(), user.getName(), user.getEmail(), user.getAge()));
        }
    }

    public void showUser(UserDto user) {
        System.out.printf("\nПользователь найден:\nID: %d\nИмя: %s\nEmail: %s\nВозраст: %d\nСоздан: %s\n",
                user.getId(), user.getName(), user.getEmail(), user.getAge(), user.getCreatedAt());
    }

    public void showSuccessMessage(String message) {
        System.out.println(message);
    }

    public void showErrorMessage(String message) {
        System.out.println("Ошибка: " + message);
    }

    public void showExitMessage() {
        System.out.println("Выход из приложения");
    }
}