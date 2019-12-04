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

/**
 * <h2>FileService для работы с файлами</h2>
 */
@Log4j
public class FileService {
    private Path path;

    /**
     * Подготовка к получению файла
     *
     * @return this
     */
    public static FileService upload() {
        return new FileService();
    }

    /**
     * Создание файла на диске. Генерация уникального имени.
     *
     * @param dir      папка в пути к файлу
     * @param id       папка в пути к файлу
     * @param filename имя получаемого файла
     * @return this
     */
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

    /**
     * Загрузка файла
     *
     * @param file
     * @return this
     */
    public FileService transfer(FilePart file) {
        file.transferTo(path);
        return this;
    }

    /**
     * Возвращает уникальное имя файла
     *
     * @return filename
     */
    public String getFilename() {
        return path.getFileName().toString();
    }

    /**
     * Удаляет файл
     *
     * @param pathFile
     * @return false - при удалении произошло исключение
     */
    public static boolean delete(String pathFile) {
        try {
            return Files.deleteIfExists(Paths.get(String.format("%s/%s", Const.PATH_FILE, pathFile)));
        } catch (IOException e) {
            log.warn(String.format("Ошибка при удалении файла: %s\n%s", pathFile, e.toString()));
            return false;
        }
    }

    /**
     * Удаляет папку с файлами
     *
     * @param dir папка из которой удалить
     * @param id  папка которую удалить
     * @return false - при удалении файлов произошло исключение
     */
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