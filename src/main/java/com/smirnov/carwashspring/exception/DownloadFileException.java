package com.smirnov.carwashspring.exception;

public class DownloadFileException extends RuntimeException {
    private static final String MESSAGE = " не добавлен на сервер. ";

    public DownloadFileException(String nameFile) {
        super(nameFile + MESSAGE);
    }

    public DownloadFileException(String nameFile, String info) {
        super(nameFile + MESSAGE + info);
    }
}
