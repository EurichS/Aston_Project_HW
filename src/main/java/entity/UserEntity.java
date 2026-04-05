package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая пользователя в системе.
 * <p>
 * Хранит персональные данные, такие как имя, уникальный email и возраст.
 * Сопоставляется с таблицей {@code users} в базе данных.
 */
@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserEntity {

    /**
     * Уникальный идентификатор пользователя.
     * Использует {@link GenerationType#SEQUENCE} для оптимизации вставок.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_gen")
    @SequenceGenerator(
            name = "users_seq_gen",
            sequenceName = "users_id_seq",
            allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Имя пользователя. Ограничено 100 символами.
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Уникальный адрес электронной почты.
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Возраст пользователя.
     */
    @Column(name = "age")
    private Integer age;

    /**
     * Дата и время создания записи.
     * Поле помечено как {@code updatable = false}, чтобы сохранить оригинальную дату при обновлении профиля.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /*public UserEntity(String name, String email, int age) {

    }*/

    public UserEntity(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    /**
     * Автоматически устанавливает дату создания перед сохранением сущности в базу данных.
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity that)) return false;

        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null) return false;
        return getAge() != null ? getAge().equals(that.getAge()) : that.getAge() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getAge() != null ? getAge().hashCode() : 0);
        return result;
    }
}