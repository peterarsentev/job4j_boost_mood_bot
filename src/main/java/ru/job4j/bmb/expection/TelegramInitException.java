package ru.job4j.bmb.expection;

public class TelegramInitException extends RuntimeException {
    public TelegramInitException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
