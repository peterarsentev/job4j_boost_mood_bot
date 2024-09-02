package ru.job4j.bmb.model;

public record Message(
        long clientId,
        String username,
        String text) {
}
