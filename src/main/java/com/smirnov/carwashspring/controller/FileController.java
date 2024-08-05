package com.smirnov.carwashspring.controller;

import com.smirnov.carwashspring.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    /**
     * Сервисный слой работы с фалами.
     */
    private final FileService fileService;

    /**
     * Загружает файл на сервер.
     * права доступа: ADMIN
     * @param file загружаемый файл
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_ADMIN")
    public void downloadFile(@RequestParam("file") MultipartFile file) {
        log.info("POST: /file");
        fileService.downloadFile(file);
    }
}
