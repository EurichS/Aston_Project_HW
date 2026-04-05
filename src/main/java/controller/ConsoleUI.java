package controller;

import console.Validator;
import console.Viewer;
import dao.UserDAO;
import entity.UserEntity;

import java.util.Optional;
import java.util.Scanner;

/**
 * Главный контроллер пользовательского интерфейса (UI), управляющий жизненным циклом приложения.
 * <p>
 * Класс координирует работу между {@link Viewer} (отображение),
 * {@link Validator} (проверка данных) и {@link UserDAO} (логика),
 * обрабатывая ввод пользователя в бесконечном цикле до команды выхода.
 */
public class ConsoleUI {
    Viewer viewer = new Viewer();
    Validator validator = new Validator();
    Scanner scanner = new Scanner(System.in);
    UserDAO userDAO=new UserDAO();

    /**
     * Запускает основной цикл обработки команд.
     * <p>
     * Метод отображает меню и ожидает ввода пользователя. Цикл продолжается
     * до тех пор, пока не будет выбрана команда завершения работы.
     */
    public void start() {
        boolean running = true;
        while (running) {
            viewer.showMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> createUser();
                case "2" -> readUser();
                case "3" -> viewer.showUsers(userDAO.findAll());
                case "4" -> updateUser();
                case "5" -> removeUser();
                case "6" -> running = false;
                default -> viewer.showMessage("Неверный выбор.");
            }
        }
        scanner.close();
    }

    /**
     * Запускает пошаговый процесс создания нового пользователя.
     * <p>
     * Метод последовательно запрашивает имя, email и возраст,
     * выполняя валидацию каждого поля. В случае ошибки ввод прерывается
     * с выводом соответствующего уведомления.
     */
    void createUser() {
        viewer.showMessage("Создание нового пользователя");
        viewer.showMessage("Имя: ");
        Optional<String> nameOpt = validator.validateString(scanner.nextLine());
        if (nameOpt.isEmpty()) {
            viewer.showEntryError("Имя не может быть пустым.");
            return;
        }
        viewer.showMessage("Email: ");
        Optional<String> emailOpt = validator.validateEmail(scanner.nextLine());
        if (emailOpt.isEmpty()) {
            viewer.showEntryError("Некорректный формат Email.");
            return;
        }
        viewer.showMessage("Возраст: ");
        Optional<Integer> ageOpt = validator.parseAge(scanner.nextLine());
        if (ageOpt.isEmpty()) {
            viewer.showEntryError("Возраст должен быть числом от 0 до 120.");
            return;
        }
        Optional<UserEntity> userSaved = userDAO.create(new UserEntity(nameOpt.get(), emailOpt.get(), ageOpt.get()));
        if (userSaved.isPresent()) {
            viewer.showMessage("Пользователь успешно сохранен.");
        } else {
            viewer.showMessage("Ошибка сохранения");
        }
    }

    /**
     * Инициирует поиск пользователя по его идентификатору.
     * <p>
     * Запрашивает ID, проверяет его формат и, если пользователь найден,
     * передает его данные в {@link Viewer} для отображения.
     */
    void readUser() {
        viewer.showMessage("Введите ID: ");
        validator.parseId(scanner.nextLine()).ifPresentOrElse(
                id -> userDAO.read(id).ifPresentOrElse(viewer::showUser,
                        () -> viewer.showMessage("Пользователь не найден.")),
                () -> viewer.showEntryError("Некорректный ID.")
        );
    }

    /**
     * Выполняет процедуру обновления данных существующего пользователя.
     * <p>
     * Позволяет выборочно изменить имя или email. Если при вводе
     * оставить поле пустым, текущее значение сохраняется.
     */
    void updateUser() {
        viewer.showMessage("Введите ID пользователя для редактирования: ");
        validator.parseId(scanner.nextLine()).ifPresent(id -> {
            userDAO.read(id).ifPresentOrElse(user -> {
                viewer.showMessage("Новое имя (" + user.getName() + "): ");
                String name = scanner.nextLine();
                if (!name.isBlank()) user.setName(name);
                viewer.showMessage("Новый email (" + user.getEmail() + "): ");
                String email = scanner.nextLine();
                if (!email.isBlank()) user.setEmail(email);
                userDAO.update(user);
                viewer.showMessage("Данные обновлены.");
            }, () -> viewer.showMessage("Пользователь не найден."));
        });
    }

    /**
     * Удаляет пользователя из системы по введенному ID.
     * <p>
     * Выполняет парсинг идентификатора и делегирует удаление слою доступа к данным.
     */
    void removeUser() {
        viewer.showMessage("Введите ID для удаления: ");
        validator.parseId(scanner.nextLine()).ifPresentOrElse(
                id -> {
                    userDAO.remove(id);
                    viewer.showMessage("Пользователь удален (если он существовал).");
                },
                () -> viewer.showEntryError("Некорректный ID.")
        );
    }
}
