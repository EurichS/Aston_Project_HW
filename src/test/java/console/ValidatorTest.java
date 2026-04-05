package console;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты для класса {@link Validator}, проверяющие корректность валидации и парсинга входных данных.
 */
class ValidatorTest {
    private final Validator validator = new Validator();
    /**
     * Тестирует метод {@link Validator#validateString(String)} с валидной строкой.
     * <p>
     * Ожидаемое поведение: возвращает {@link Optional} с обрезанной строкой.
     */
    @Test
    @DisplayName("validateString с валидной непустой строкой должен вернуть Optional с обрезанным значением")
    void validateString_ValidInput_TrimmedString() {

        String input = "  John Doe  ";

        Optional<String> result = validator.validateString(input);

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get());
    }
    /**
     * Тестирует метод {@link Validator#validateString(String)} с пустой строкой.
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}.
     */
    @Test
    @DisplayName("validateString с пустой строкой должен вернуть пустой Optional")
    void validateString_WithEmptyString_EmptyOptional() {
        String input = "";

        Optional<String> result = validator.validateString(input);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#validateString(String)} со строкой из пробелов.
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}.
     */
    @Test
    @DisplayName("validateString со строкой из пробелов должен вернуть пустой Optional")
    void validateString_WithSpaceOnly_EmptyOptional() {
        String input = "   ";

        Optional<String> result = validator.validateString(input);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#validateString(String)} с null‑входом.
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}.
     */
    @Test
    @DisplayName("validateString с null должен вернуть пустой Optional")
    void validateString_NullInput_EmptyOptional() {

        Optional<String> result = validator.validateString(null);


        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#validateEmail(String)} с корректным email.
     * <p>
     * Ожидаемое поведение: возвращает {@link Optional} с email.
     */
    @Test
    @DisplayName("validateEmail с корректным email должен вернуть Optional с email")
    void validateEmail_ValidEmail_Email() {
        String input = "user@example.com";

        Optional<String> result = validator.validateEmail(input);

        assertTrue(result.isPresent());
        assertEquals("user@example.com", result.get());
    }

    /**
     * Тестирует метод {@link Validator#validateEmail(String)} с некорректным email (без @).
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}.
     */
    @Test
    @DisplayName("validateEmail с некорректным email (без @) должен вернуть пустой Optional")
    void validateEmail_WithoutAtSymbol_EmptyOptional() {
        String input = "userexample.com";

        Optional<String> result = validator.validateEmail(input);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#validateEmail(String)} с пустым email.
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}.
     */
    @Test
    @DisplayName("validateEmail с пустой строкой должен вернуть пустой Optional")
    void validateEmail_EmptyString_EmptyOptional() {
        String input = "";

        Optional<String> result = validator.validateEmail(input);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#validateEmail(String)} с null‑email.
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}.
     */
    @Test
    @DisplayName("validateEmail с null должен вернуть пустой Optional")
    void validateEmail_NullInput_EmptyOptional() {
        Optional<String> result = validator.validateEmail(null);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#parseAge(String)} с корректным возрастом (в диапазоне 0–90).
     * <p>
     * Ожидаемое поведение: возвращает {@link Optional} с возрастом.
     */
    @Test
    @DisplayName("parseAge с корректным возрастом (0–90) должен вернуть Optional с возрастом")
    void parseAge_ValidAge_Age() {
        String input = "25";

        Optional<Integer> result = validator.parseAge(input);

        assertTrue(result.isPresent());
        assertEquals(25, result.get());
    }

    /**
     * Тестирует метод {@link Validator#parseAge(String)} с возрастом вне диапазона (отрицательный).
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}.
     */
    @Test
    @DisplayName("parseAge с отрицательным возрастом должен вернуть пустой Optional")
    void parseAge_NegativeAge_EmptyOptional() {
        String input = "-5";

        Optional<Integer> result = validator.parseAge(input);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#parseAge(String)} с возрастом вне диапазона (> 90).
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}.
     */
    @Test
    @DisplayName("parseAge с возрастом > 90 должен вернуть пустой Optional")
    void parseAge_AgeOver90_EmptyOptional() {
        String input = "95";

        Optional<Integer> result = validator.parseAge(input);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#parseAge(String)} с нечисловой строкой.
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}.
     */
    @Test
    @DisplayName("parseAge с нечисловой строкой должен вернуть пустой Optional")
    void parseAge_NonNumericString_EmptyOptional() {
        String input = "abc";

        Optional<Integer> result = validator.parseAge(input);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#parseAge(String)} с null‑входом.
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}.
     */
    @Test
    @DisplayName("parseAge с null должен вернуть пустой Optional")
    void parseAge_NullInput_EmptyOptional() {
        Optional<Integer> result = validator.parseAge(null);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#parseId(String)} с корректным ID.
     * <p>
     * Ожидаемое поведение: возвращает {@link Optional} с ID.
     */
    @Test
    @DisplayName("parseId с корректным числовым ID должен вернуть Optional с ID")
    void parseId_ValidId_Id() {
        String input = "12345";

        Optional<Long> result = validator.parseId(input);

        assertTrue(result.isPresent());
        assertEquals(12345L, result.get());
    }
    /**
     * Тестирует метод {@link Validator#parseId(String)} с некорректным ID (нечисловая строка).
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}.
     */
    @Test
    @DisplayName("parseId с нечисловой строкой должен вернуть пустой Optional")
    void parseId_NonNumericString_EmptyOptional() {
        String input = "abc123";

        Optional<Long> result = validator.parseId(input);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#parseId(String)} со строкой, содержащей дробное число.
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}, так как Long.parseLong не принимает дробные числа.
     */
    @Test
    @DisplayName("parseId со строкой с дробным числом должен вернуть пустой Optional")
    void parseId_DecimalNumber_EmptyOptional() {
        String input = "123.45";

        Optional<Long> result = validator.parseId(input);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#parseId(String)} с пустой строкой.
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}.
     */
    @Test
    @DisplayName("parseId с пустой строкой должен вернуть пустой Optional")
    void parseId_EmptyString_EmptyOptional() {
        String input = "";

        Optional<Long> result = validator.parseId(input);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#parseId(String)} с null‑входом.
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}.
     */
    @Test
    @DisplayName("parseId с null должен вернуть пустой Optional")
    void parseId_NullInput_EmptyOptional() {
        Optional<Long> result = validator.parseId(null);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#parseId(String)} с очень большим числом (выходящим за диапазон Long).
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional} из‑за NumberFormatException.
     */
    @Test
    @DisplayName("parseId с числом, превышающим диапазон Long, должен вернуть пустой Optional")
    void parseId_OutOfLongRange_EmptyOptional() {
        String input = "99999999999999999999"; // слишком большое число

        Optional<Long> result = validator.parseId(input);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#validateEmail(String)} с email, содержащим пробелы.
     * <p>
     * Ожидаемое поведение: возвращает пустой {@link Optional}, так как пробелы недопустимы в email.
     */
    @Test
    @DisplayName("validateEmail с email, содержащим пробелы, должен вернуть пустой Optional")
    void validateEmail_SpacesInEmail_EmptyOptional() {
        String input = "user @example.com";

        Optional<String> result = validator.validateEmail(input);

        assertFalse(result.isPresent());
    }

    /**
     * Тестирует метод {@link Validator#validateEmail(String)} с email в верхнем регистре.
     * <p>
     * Ожидаемое поведение: возвращает {@link Optional} с email (регистр не влияет на валидность).
     */
    @Test
    @DisplayName("validateEmail с email в верхнем регистре должен вернуть Optional с email")
    void validateEmail_UpperCaseEmail_Email() {
        String input = "USER@EXAMPLE.COM";

        Optional<String> result = validator.validateEmail(input);

        assertTrue(result.isPresent());
        assertEquals("USER@EXAMPLE.COM", result.get());
    }

    /**
     * Тестирует метод {@link Validator#validateEmail(String)} с email, имеющим несколько доменов.
     * <p>
     * Ожидаемое поведение: возвращает {@link Optional} с email, если формат соответствует шаблону.
     */
    @Test
    @DisplayName("validateEmail с email с многоуровневым доменом должен вернуть Optional с email")
    void validateEmail_MultiLevelDomain_Email() {
        String input = "user@sub.domain.example.com";

        Optional<String> result = validator.validateEmail(input);

        assertTrue(result.isPresent());
        assertEquals("user@sub.domain.example.com", result.get());
    }
}