package controller;

import console.Validator;
import console.Viewer;
import dao.UserDAO;
import entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleUITest {

    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private UserDAO userDAO;
    private ConsoleUI consoleUI;
    @Spy
    ConsoleUITest consoleUITest;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        userDAO = new UserDAO();
        Validator validator = new Validator();
        Viewer viewer = new Viewer();
        consoleUI = new ConsoleUI();
        consoleUI.userDAO = userDAO;
        consoleUI.validator = validator;
        consoleUI.viewer = viewer;
    }

    @Test
    void readUser_ValidId_ShouldShowUser() {
        UserEntity user = new UserEntity("John", "john@example.com", 25);
        userDAO.create(user);
        String input = user.getId().toString() + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        consoleUI.scanner = new Scanner(System.in);
        consoleUI.readUser();
        String output = outputStream.toString();
        assertTrue(output.contains("Введите ID: "), "Должно быть запрошено ID");
        assertTrue(output.contains("John"), "Должны быть показаны данные пользователя");
    }

    @Test
    void readUser_InvalidId_ShouldShowError() {
        String input = "abc\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        consoleUI.scanner = new Scanner(System.in);
        consoleUI.readUser();
        String output = outputStream.toString();
        assertTrue(output.contains("Некорректный ID."),
                "Должно быть показано сообщение для некорректного ID");
    }

    @Test
    void updateUser_ValidIdData_UpdateUser() {
        UserEntity user = new UserEntity("OldName", "old@example.com", 30);
        userDAO.create(user);
        String input = user.getId().toString() + "\nNewName\nnew@example.com\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        consoleUI.scanner = new Scanner(System.in);
        consoleUI.updateUser();
        String output = outputStream.toString();
        assertTrue(output.contains("Данные обновлены."), "Должно быть сообщение об успешном обновлении");
        Optional<UserEntity> updatedUser = userDAO.read(user.getId());
        assertTrue(updatedUser.isPresent(), "Пользователь должен существовать после обновления");
        assertEquals("NewName", updatedUser.get().getName());
        assertEquals("new@example.com", updatedUser.get().getEmail());
    }

    @Test
    void removeUser_ValidId_RemoveUser() {
        UserEntity user = new UserEntity("ToDelete", "delete@example.com", 40);
        userDAO.create(user);
        String input = user.getId().toString() + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        consoleUI.scanner = new Scanner(System.in);
        consoleUI.removeUser();
        String output = outputStream.toString();
        assertTrue(output.contains("Пользователь удален (если он существовал)."),
                "Должно быть сообщение о удалении");

        Optional<UserEntity> deletedUser = userDAO.read(user.getId());
        assertTrue(deletedUser.isEmpty(), "Пользователь должен быть удалён из БД");
    }

    @Test
    void findAll_ShowAllUsers() {
        userDAO.create(new UserEntity("User1", "user1@example.com", 20));
        userDAO.create(new UserEntity("User2", "user2@example.com", 25));
        String input = "3\n6\n"; // показать всех, затем выход
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        consoleUI.scanner = new Scanner(System.in);
        consoleUI.start();
        String output = outputStream.toString();
        assertTrue(output.contains("User1"), "Должен быть показан первый пользователь");
        assertTrue(output.contains("User2"), "Должен быть показан второй пользователь");
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);

    }
}