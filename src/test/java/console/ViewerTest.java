package console;

import entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты для класса {@link Viewer}, проверяющие корректность вывода информации в консоль.
 * <p>
 * Для перехвата вывода в консоль используется {@link ByteArrayOutputStream}.
 */
class ViewerTest {

    private Viewer viewer;
    private ByteArrayOutputStream outputStream;

    /**
     * Настройка тестового окружения перед каждым тестом.
     * <p>
     * Сохраняет оригинальный {@link System#out}, перенаправляет вывод в
     * {@link ByteArrayOutputStream} для перехвата выводимого текста, инициализирует {@link Viewer}.
     */
    @BeforeEach
    void setUp() {
        PrintStream originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        viewer = new Viewer();
    }

    /**
     * Тестирует метод {@link Viewer#showMenu()}.
     * <p>
     * Ожидаемое поведение: выводит в консоль меню с пунктами от 1 до 6 и приглашение выбрать действие.
     */
    @Test
    void showMenu_DisplayFullMenu() {
        viewer.showMenu();

        String expectedOutput = """

                --- МЕНЮ УПРАВЛЕНИЯ ---\r
                1. Создать пользователя\r
                2. Найти пользователя по ID\r
                3. Показать всех пользователей\r
                4. Обновить пользователя\r
                5. Удалить пользователя\r
                6. Выход\r
                Выберите действие:\s""";
        assertEquals(expectedOutput, outputStream.toString());
    }

    /**
     * Тестирует метод {@link Viewer#showMessage(String)}.
     * <p>
     * Ожидаемое поведение: выводит переданное сообщение с переводом строки.
     */
    @Test
    void showMessage_ValidMessage_DisplayMessage() {
        String message = "Операция выполнена успешно!";

        viewer.showMessage(message);

        assertEquals("Операция выполнена успешно!\r\n", outputStream.toString());
    }

    /**
     * Тестирует метод {@link Viewer#showUser(UserEntity)}.
     * <p>
     * Ожидаемое поведение: выводит строку «Найден пользователь: » и строковое представление сущности.
     */
    @Test
    void showUser_ValidUser_DisplayUser() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        user.setId(1L);

        viewer.showUser(user);

        assertEquals("Найден пользователь: " + user + "\r\n", outputStream.toString());
    }

    /**
     * Тестирует метод {@link Viewer#showUsers(List)} с пустым списком.
     * <p>
     * Ожидаемое поведение: выводит сообщение «Список пользователей пуст.».
     */
    @Test
    void showUsers_EmptyList_DisplayEmptyMessage() {
        List<UserEntity> users = Collections.emptyList();

        viewer.showUsers(users);

        assertEquals("Список пользователей пуст.\r\n", outputStream.toString());
    }

    /**
     * Тестирует метод {@link Viewer#showUsers(List)} со списком из нескольких пользователей.
     * <p>
     * Ожидаемое поведение: последовательно выводит каждого пользователя из списка.
     */
    @Test
    void showUsers_MultipleUsers_DisplayAllUsers() {
        UserEntity user1 = new UserEntity("Alice", "alice@example.com", 25);
        user1.setId(1L);
        UserEntity user2 = new UserEntity("Bob", "bob@example.com", 35);
        user2.setId(2L);
        List<UserEntity> users = Arrays.asList(user1, user2);

        viewer.showUsers(users);

        String expectedOutput = user1 + "\r\n" + user2 + "\r\n";
        assertEquals(expectedOutput, outputStream.toString());
    }

    /**
     * Тестирует метод {@link Viewer#showEntryError(String)}.
     * <p>
     * Ожидаемое поведение: выводит сообщение об ошибке с указанием поля и предложением повторить ввод.
     */
    @Test
    void showEntryError_FieldName_DisplayError() {
        String field = "возраст";

        viewer.showEntryError(field);

        assertEquals("Ошибка ввода в поле: возраст. Попробуйте снова.\r\n", outputStream.toString());
    }
}