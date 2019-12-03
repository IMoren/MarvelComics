package ru.stm.marvelcomics.service;

import lombok.extern.log4j.Log4j;
import org.springframework.http.codec.multipart.FilePart;
import ru.stm.marvelcomics.util.Const;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;

@Log4j
public class FileService {
    Path path;

    public static FileService upload() {
        return new FileService();
    }

    public FileService createPath(String dir, long id, String filename) {
        String uploadDirectory = String.format("%s%s/%d/", Const.PATH_FILE, dir, id);
        if (!Files.exists(Paths.get(uploadDirectory))) {
            try {
                Files.createDirectories(Paths.get(uploadDirectory));
            } catch (IOException e) {
                log.warn(String.format("Ошибка при создании директории: %s\n%s", uploadDirectory, e.toString()));
            }
        }

        String uuidFileName = String.format("%s_%s", UUID.randomUUID().toString(), filename);

        try {
            path = Files.createFile(Paths.get(String.format("%s/%s", uploadDirectory, uuidFileName)));
        } catch (IOException e) {
            log.warn(String.format("Ошибка при создании файла: %s/%s\n%s", uploadDirectory, uuidFileName, e.toString()));
        }
        return this;
    }

    public FileService transfer (FilePart file){
        file.transferTo(path);
        return this;
    }

    public String getFilename(){
        return path.getFileName().toString();
    }


    public static boolean delete(String pathFile) {
        try {
            return Files.deleteIfExists(Paths.get(String.format("%s/%s", Const.PATH_FILE, pathFile)));
        } catch (IOException e) {
            log.warn(String.format("Ошибка при удалении файла: %s\n%s", pathFile, e.toString()));
            return false;
        }
    }

    public static boolean delete(String dir, long id) {
        Path path = Paths.get(String.format("%s%s/%d", Const.PATH_FILE, dir, id));
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            log.info(String.format("Удалена папка с файлами: %s/%d", dir, id));
        } catch (IOException e) {
            log.warn(String.format("Ошибка при удалении папки с файлами: %s/%d\n%s", dir, id, e.toString()));
            return false;
        }
        return true;
    }
}