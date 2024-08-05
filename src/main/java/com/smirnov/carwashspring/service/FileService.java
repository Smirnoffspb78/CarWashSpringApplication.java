package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.exception.DownloadFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@Service
@Slf4j
public class FileService {

    @Value("${upload.path}")
    private String path;

    public void downloadFile(MultipartFile file) {
        if (!file.isEmpty()) {
            String nameFile = file.getOriginalFilename();
            try {
                File newFile = new File("%s/%s".formatted(path, nameFile));
                if (newFile.exists()) {
                    throw new DownloadFileException(nameFile, "Файл уже существует");
                }
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(newFile));
                stream.write(bytes);
                stream.close();
                log.info("Файл загружен на сервер");
            } catch (Exception e) {
                throw new DownloadFileException(nameFile);
            }
        } else {
            throw new DownloadFileException("Содержимое не найдено");
        }
    }
}
