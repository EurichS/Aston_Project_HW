package com.example.common_models.event;

public record UserEvent(UserOperation operation, String email) {
}
