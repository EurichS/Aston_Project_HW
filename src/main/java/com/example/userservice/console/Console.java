package com.example.userservice.console;

import com.example.userservice.dto.UserDto;
import com.example.userservice.sercice.UserService;
import org.springframework.stereotype.Component;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

@Component
public class Console {

    private final UserService userService;

    private final Scanner scanner = new Scanner(System.in);

    private final Viewer viewer = new Viewer();

    public Console(UserService userService) {
        this.userService = userService;
    }

    public void start() {
        while (true) {
            viewer.showMenu();
            int choice = getValidMenuChoice();

            switch (choice) {
                case 1 -> createUser();
                case 2 -> getAllUsers();
                case 3 -> getUserById();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 0 -> {
                    viewer.showExitMessage();
                    return;
                }
                default -> viewer.showErrorMessage("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private int getValidMenuChoice() {
        while (true) {
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                return choice;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                viewer.showErrorMessage("Пожалуйста, введите число от 0 до 5.");
            }
        }
    }

    private void createUser() {
        try {
            System.out.print("Имя: ");
            String name = scanner.nextLine();

            if (Validator.isValidName(name)) {
                viewer.showErrorMessage("Имя не может быть пустым");
                return;
            }

            System.out.print("Email: ");
            String email = scanner.nextLine();

            if (Validator.isValidEmail(email)) {
                viewer.showErrorMessage("Некорректный формат email");
                return;
            }

            System.out.print("Возраст: ");
            Integer age = getValidAgeInput();

            UserDto userDto = new UserDto();
            userDto.setName(name);
            userDto.setEmail(email);
            userDto.setAge(age);

            UserDto created = userService.createUser(userDto);
            viewer.showSuccessMessage("Пользователь создан с ID: " + created.getId());
        } catch (Exception e) {
            viewer.showErrorMessage("Ошибка при создании пользователя: " + e.getMessage());
        }
    }

    private Integer getValidAgeInput() {
        while (true) {
            try {
                Integer age = scanner.nextInt();
                scanner.nextLine();
                if (Validator.isValidAge(age)) {
                    return age;
                } else {
                    viewer.showErrorMessage("Возраст должен быть положительным числом");
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();
                viewer.showErrorMessage("Пожалуйста, введите корректное число для возраста.");
            }
        }
    }

    private void getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        viewer.showAllUsers(users);
    }

    private void getUserById() {
        try {
            System.out.print("Введите ID пользователя: ");
            Long id = getValidIdInput();

            UserDto user = userService.getUserById(id);
            viewer.showUser(user);
        } catch (Exception e) {
            viewer.showErrorMessage("Пользователь не найден: " + e.getMessage());
        }
    }

    private Long getValidIdInput() {
        while (true) {
            try {
                Long id = scanner.nextLong();
                scanner.nextLine();
                if (Validator.isValidId(id)) {
                    return id;
                } else {
                    viewer.showErrorMessage("ID должен быть положительным числом");
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();
                viewer.showErrorMessage("Пожалуйста, введите корректное число для ID.");
            }
        }
    }

    private void updateUser() {
        try {
            System.out.print("Введите ID пользователя для обновления: ");
            Long id = getValidIdInput();

            System.out.print("Новое имя: ");
            String name = scanner.nextLine();

            if (Validator.isValidName(name)) {
                viewer.showErrorMessage("Имя не может быть пустым");
                return;
            }

            System.out.print("Новый email: ");
            String email = scanner.nextLine();

            if (Validator.isValidEmail(email)) {
                viewer.showErrorMessage("Некорректный формат email");
                return;
            }

            System.out.print("Новый возраст: ");
            Integer age = getValidAgeInput();

            UserDto userDto = new UserDto();
            userDto.setName(name);
            userDto.setEmail(email);
            userDto.setAge(age);

            UserDto updated = userService.updateUser(id, userDto);
            viewer.showSuccessMessage("Пользователь успешно обновлён. ID: " + updated.getId());
        } catch (Exception e) {
            viewer.showErrorMessage("Ошибка при обновлении пользователя: " + e.getMessage());
        }
    }

    private void deleteUser() {
        try {
            System.out.print("Введите ID пользователя для удаления: ");
            Long id = getValidIdInput();

            userService.deleteUser(id);
            viewer.showSuccessMessage("Пользователь с ID " + id + " успешно удалён.");
        } catch (Exception e) {
            viewer.showErrorMessage("Ошибка при удалении пользователя: " + e.getMessage());
        }
    }
}