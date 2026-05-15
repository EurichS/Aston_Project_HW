package com.example.common_models.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserOperation {

    CREATE("Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан."),
    DELETE("Здравствуйте! Ваш аккаунт был удалён.");

    private final String message;
}
